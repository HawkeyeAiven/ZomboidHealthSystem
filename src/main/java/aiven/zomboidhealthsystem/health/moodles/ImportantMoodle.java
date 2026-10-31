package aiven.zomboidhealthsystem.health.moodles;

import aiven.zomboidhealthsystem.health.Health;
import aiven.zomboidhealthsystem.utility.Util;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;

public abstract class ImportantMoodle extends SimpleMoodle {
    public ImportantMoodle(Health health) {
        super(health);
    }

    public abstract boolean canPlayerLive();

    protected abstract void _update(float tickDelta);

    protected abstract ResourceKey<DamageType> getDamageType();

    @Override
    public final void update(float tickDelta) {
        if(!canPlayerLive()) {
            die();
        }
        _update(tickDelta);
    }

    protected void die() {
        health.die(Util.getDamageSource(getDamageType(), getPlayer().level()));
    }
}
