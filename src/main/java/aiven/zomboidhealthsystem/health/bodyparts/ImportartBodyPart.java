package aiven.zomboidhealthsystem.health.bodyparts;

import aiven.zomboidhealthsystem.health.Health;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;

public abstract class ImportartBodyPart extends BodyPart {

    public ImportartBodyPart(Health health, float hp, Player player, String name) {
        super(health, hp, player, name);
    }

    public boolean canPlayerLive() {
        return hp > 0;
    }

    @Override
    public float damage(float amount, DamageSource source) {
        onDamage(amount);
        return super.damage(amount, source);
    }

    protected abstract void onDamage(float amount);
}