package aiven.zomboidhealthsystem.health.moodles;

import aiven.zomboidhealthsystem.health.Health;
import aiven.zomboidhealthsystem.utility.Util;

import net.minecraft.world.effect.MobEffects;

public final class Drunkenness extends SimpleMoodle {
    public Drunkenness(Health health) {
        super(health);
    }

    @Override
    public String getId() {
        return "drunkenness";
    }

    @Override
    public void update(float tickDelta) {
        if(tickDelta != 0) {
            this.addValue(-1.0F / (10 * 60 * 20) * tickDelta);
            this.getHealth().getPain().setMultiplier(this, 1.0F / (this.getValue() * 1.5F + 1));
            this.getHealth().getExhaustion().setMultiplier(this, getValue() / 2 + 1);
            if (getValue() >= 1) {
                if (Util.random(1.0F / (int) (0.75F * 60 * 20 / getValue()))) {
                    this.getHealth().addMobEffect(MobEffects.CONFUSION, 0, 10 * 20);
                }
                if (Util.random(1.0F / (int) (0.75F * 60 * 20 / getValue()))) {
                    this.getHealth().addMobEffect(MobEffects.BLINDNESS, 0, 5 * 20);
                }
            }
        }
    }

    @Override
    public void sleep(int sumTicks) {
        this.addValue(-1.0F / 1000 * sumTicks);
    }

    @Override
    public float maxValue() {
        return 5.0F;
    }
}
