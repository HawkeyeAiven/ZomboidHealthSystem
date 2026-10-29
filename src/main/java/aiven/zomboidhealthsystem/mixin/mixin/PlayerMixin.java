package aiven.zomboidhealthsystem.mixin.mixin;

import aiven.zomboidhealthsystem.ZomboidHealthSystem;
import aiven.zomboidhealthsystem.health.bodyparts.BodyPart;
import aiven.zomboidhealthsystem.item.AbstractBandageItem;
import aiven.zomboidhealthsystem.mixin.access.PlayerAccess;
import aiven.zomboidhealthsystem.health.Health;
import aiven.zomboidhealthsystem.health.moodles.Hunger;

import com.mojang.authlib.GameProfile;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stats;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Player.class, priority = 10000)
public abstract class PlayerMixin extends LivingEntityMixin implements PlayerAccess {

    @Shadow
    public abstract float getAbsorptionAmount();

    @Shadow
    public abstract void awardStat(ResourceLocation resourceLocation, int i);

    @Shadow
    public abstract @Nullable ItemEntity drop(ItemStack itemStack, boolean bl, boolean bl2);

    @Unique
    private static final String healthId = ZomboidHealthSystem.ID + ".health";

    @Unique
    private Health modHealth;

    @Inject(at = @At("RETURN"), method = "<init>")
    public void init(Level level, BlockPos blockPos, float f, GameProfile gameProfile, CallbackInfo ci) {
        modHealth = new Health(toPlayer());
    }

    @Inject(at = @At("HEAD"), method = "readAdditionalSaveData")
    public void readData(CompoundTag compoundTag, CallbackInfo ci) {
        byte[] bytes = compoundTag.getByteArray(healthId);
        if(bytes.length != 0) {
            try {
                modHealth.readNbt(bytes);
            } catch (Exception e) {
                ZomboidHealthSystem.log.error("Failed to load read \"{}\"'s data", toPlayer().getName().getString(), e);
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "addAdditionalSaveData")
    public void addData(CompoundTag compoundTag, CallbackInfo ci) {
        compoundTag.putByteArray(healthId, modHealth.getNbt());
    }

    /**
     * @author Aiven
     * @reason Перенаправление урона
     */
    @Overwrite
    public void actuallyHurt(DamageSource damageSource, float damage) {
        if (!this.isInvulnerableTo(damageSource)) {
            damage = this.getDamageAfterArmorAbsorb(damageSource, damage);
            damage = this.getDamageAfterMagicAbsorb(damageSource, damage);
            damage = Math.max(damage - this.getAbsorptionAmount(), 0.0F);

            if (damage != 0.0F) {
                this.getCombatTracker().recordDamage(damageSource, damage);
                Health health = zomboid_health_system$getModHealth();
                health.damage(damageSource, damage);
                if (damage < 3.4028235E37F) {
                    this.awardStat(Stats.DAMAGE_TAKEN, Math.round(damage * 10.0F));
                }

                this.gameEvent(GameEvent.ENTITY_DAMAGE);
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "tick")
    public void tick(CallbackInfo ci) {
        modHealth.tick();
    }

    @Inject(at = @At("RETURN"), method = "dropEquipment")
    public void dropEquipment(CallbackInfo ci) {
        Health health = zomboid_health_system$getModHealth();
        for(BodyPart bodyPart : health.getBodyParts()) {
            if(bodyPart.isBandaged()) {
                AbstractBandageItem bandageItem = bodyPart.unBandage();
                drop(bandageItem.getDefaultInstance(), true, false);
            }
        }
    }

    /**
     * @author Aiven
     * @reason Перенаправление на {@link Hunger#canEat()}
     */
    @Overwrite
    public boolean canEat(boolean bl) {
        return modHealth.getHunger().canEat();
    }

    @Override
    public Health zomboid_health_system$getModHealth() {
        return modHealth;
    }

    @Override
    public void zomboid_health_system$setModHealth(Health health) {
        this.modHealth = health;
    }

    @Unique
    public final Player toPlayer() {
        return (Player) ((Object) (this));
    }
}
