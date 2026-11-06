package aiven.zomboidhealthsystem.mixin.mixin;

import aiven.zomboidhealthsystem.health.Health;
import aiven.zomboidhealthsystem.mixin.access.PlayerAccess;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.damagesource.CombatTracker;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = LivingEntity.class, priority = 10000)
public abstract class LivingEntityMixin extends EntityMixin {

    @Shadow
    public abstract void setHealth(float f);

    @Shadow
    protected abstract float getDamageAfterArmorAbsorb(DamageSource damageSource, float f);

    @Shadow
    protected abstract float getDamageAfterMagicAbsorb(DamageSource damageSource, float f);

    @Shadow
    public abstract CombatTracker getCombatTracker();

    @Shadow
    @Final
    private static EntityDataAccessor<Float> DATA_HEALTH_ID;

    /**
     * @author Aiven
     * @reason К сожалению, вместо того, чтобы использовать метод isAlive(), майнкрафт часто обращается к этому методу.
     */
    @Overwrite
    public float getHealth() {
        if(toLivingEntity() instanceof PlayerAccess playerAccess) {
            Health health = playerAccess.zomboid_health_system$getModHealth();
            if(!health.isAlive()) {
                return 0.0F;
            }
        }
        return this.entityData.get(DATA_HEALTH_ID);
    }

    /**
     * @author Aiven
     * @reason Перенаправление на {@link Health#isDeadOrDying()} )}
     */
    @Overwrite
    public boolean isDeadOrDying() {
        if(toLivingEntity() instanceof PlayerAccess playerAccess) {
            return playerAccess.zomboid_health_system$getModHealth().isDeadOrDying() || getHealth() == 0;
        } else {
            return this.getHealth() <= 0.0F;
        }
    }

    /**
     * @author Aiven
     * @reason Перенаправление на {@link Health#isAlive()}
     */
    @Overwrite
    public boolean isAlive() {
        if(toLivingEntity() instanceof PlayerAccess playerAccess) {
            return playerAccess.zomboid_health_system$getModHealth().isAlive() && getHealth() != 0;
        } else {
            return !this.isRemoved() && this.getHealth() > 0.0F;
        }
    }


    /**
     * @author Aiven
     * @reason Перенаправление на {@link Health#healRandom(float)} )}
     */
    @Overwrite
    public void heal(float value) {
        if(toLivingEntity() instanceof PlayerAccess playerAccess) {
            playerAccess.zomboid_health_system$getModHealth().healRandom(value);
        } else {
            float g = this.getHealth();
            if (g > 0.0F) {
                this.setHealth(g + value);
            }
        }
    }

    @Inject(at = @At("RETURN"), method = "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z")
    private void addEffect(MobEffectInstance mobEffectInstance, Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (!level().isClientSide()) {
            if (toLivingEntity() instanceof PlayerAccess playerAccess) {
                if (mobEffectInstance.getEffect().equals(MobEffects.ABSORPTION)) {
                    Health health = playerAccess.zomboid_health_system$getModHealth();
                    int addHp = (mobEffectInstance.getAmplifier() + 1) * 4;
                    if(health.getAdditionalHpSum() < addHp) {
                        health.setAdditionalHp(addHp);
                    }
                }
            }
        }
    }

    @Unique
    public final LivingEntity toLivingEntity() {
        return (LivingEntity) ((Object)this);
    }
}
