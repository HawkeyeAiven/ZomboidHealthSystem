package aiven.zomboidhealthsystem.health.bodyparts;

import aiven.zomboidhealthsystem.health.Health;

import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

import java.util.concurrent.atomic.AtomicReference;

public final class Head extends ImportartBodyPart {

    public Head(Health health, float hp, Player player) {
        super(health, hp, player, Health.HEAD_ID);
    }

    @Override
    protected void onDamage(float amount) {
        if (amount >= 1) {
            getHealth().addMobEffect(MobEffects.BLINDNESS, 10 * 20, 0);
            this.health.getBlood().addValue(-amount * 5.0F);
        }
    }

    @Override
    public float getMaxHp() {
        return Health.MAX_HEAD_HP;
    }

    @Override
    public void update() {
        super.update();
        if (this.getMaxHp() / 2 >= this.getHp()) {
            if(Health.once(3 * 60 * 20)){
                getHealth().addMobEffect(MobEffects.BLINDNESS, 10 * 20, 0);
            }
        }
    }

    @Override
    public void addEffectAmplifiers(AtomicReference<Float> slowness, AtomicReference<Float> fatigue, AtomicReference<Float> weakness) {

    }
}
