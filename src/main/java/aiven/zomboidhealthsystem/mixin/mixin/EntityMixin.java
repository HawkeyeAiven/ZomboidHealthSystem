package aiven.zomboidhealthsystem.mixin.mixin;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;


@Mixin(value = Entity.class, priority = 10000)
public abstract class EntityMixin {
    @Shadow
    public abstract void gameEvent(GameEvent gameEvent);

    @Shadow
    public abstract double getY();

    @Shadow
    public abstract void playSound(SoundEvent soundEvent, float f, float g);

    @Shadow
    public abstract double getZ();

    @Shadow
    public abstract double getX();

    @Shadow
    protected abstract void markHurt();

    @Shadow
    public int invulnerableTime;

    @Shadow
    public abstract EntityType<?> getType();

    @Shadow
    public abstract boolean isInvulnerableTo(DamageSource damageSource);

    @Shadow
    public abstract Level level();
}
