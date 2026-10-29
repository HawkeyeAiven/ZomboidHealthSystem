package aiven.zomboidhealthsystem.health.bodyparts;


import aiven.zomboidhealthsystem.health.Health;

import net.minecraft.world.entity.player.Player;

import java.util.concurrent.atomic.AtomicReference;

public final class Arm extends BodyPart {

    public Arm(Health health, float hp, Player player, String name) {
        super(health, hp, player, name);
    }

    @Override
    public void addEffectAmplifiers(AtomicReference<Float> slowness, AtomicReference<Float> fatigue, AtomicReference<Float> weakness) {
        fatigue.set(fatigue.get() + ((getMaxHp() - getHp()) / 3.0F));
        weakness.set(weakness.get() + ((getMaxHp() - getHp()) / 3.0F));
    }

    @Override
    public float getMaxHp() {
        return Health.MAX_ARM_HP;
    }
}
