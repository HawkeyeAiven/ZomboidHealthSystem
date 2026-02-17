package aiven.zomboidhealthsystem.foundation.mixin;

import aiven.zomboidhealthsystem.foundation.player.Health;
import aiven.zomboidhealthsystem.foundation.world.ModServer;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;

@Mixin(value = LivingEntity.class, priority = 10000)
public abstract class LivingEntityMixin extends EntityMixin {

    @Shadow public abstract boolean isUsingRiptide();

    @Shadow public abstract boolean isFallFlying();

    @Shadow protected abstract float applyArmorToDamage(DamageSource source, float amount);

    @Shadow protected abstract float modifyAppliedDamage(DamageSource source, float amount);

    @Shadow protected abstract float getJumpVelocity();

    @Shadow @Nullable public abstract StatusEffectInstance getStatusEffect(StatusEffect effect);

    @Shadow public abstract void onDeath(DamageSource damageSource);

    @Shadow public abstract float getHealth();

    @Shadow protected int despawnCounter;

    @Shadow public abstract boolean isDead();

    @Shadow public abstract boolean isSleeping();

    @Shadow public abstract boolean blockedByShield(DamageSource source);

    @Shadow @Final public LimbAnimator limbAnimator;

    @Shadow protected float lastDamageTaken;

    @Shadow public int maxHurtTime;

    @Shadow public int hurtTime;

    @Shadow public abstract void setAttacker(@Nullable LivingEntity attacker);

    @Shadow protected int playerHitTimer;

    @Shadow @Nullable protected PlayerEntity attackingPlayer;

    @Shadow public abstract void takeKnockback(double strength, double x, double z);

    @Shadow public abstract void tiltScreen(double deltaX, double deltaZ);

    @Shadow public abstract boolean hasStatusEffect(StatusEffect effect);

    @Shadow public abstract boolean damage(DamageSource source, float amount) throws IOException;

    @Shadow public abstract void heal(float amount);

    @Shadow public abstract void setHealth(float health);

    @Shadow public abstract void wakeUp();

    @Inject(at = @At("HEAD"), method = "addStatusEffect(Lnet/minecraft/entity/effect/StatusEffectInstance;Lnet/minecraft/entity/Entity;)Z")
    private void addStatusEffect(StatusEffectInstance effect, Entity source, CallbackInfoReturnable<Boolean> cir) {
        if(!this.getWorld().isClient && this.isPlayer() && effect.getEffectType().equals(StatusEffects.ABSORPTION)) {
            try {
                Health health = ModServer.getHealth((PlayerEntity) ((Object) this));
                for(Health.BodyPart bodyPart : health.getBodyParts()) {
                    bodyPart.setAdditionalHp(Math.max(bodyPart.getAdditionalHp(), ((effect.getAmplifier() + 1.0F) * 4) / health.getBodyParts().length));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}