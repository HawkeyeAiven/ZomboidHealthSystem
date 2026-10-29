package aiven.zomboidhealthsystem.health.moodles;

import aiven.zomboidhealthsystem.health.Health;

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
    public void update() {
        this.addAmount(-1.0F / (10 * 60 * 20) * Health.UPDATE_FREQUENCY);
        this.getHealth().getPain().setMultiplier(this, 1.0F / (this.getAmount() * 1.5F + 1));
        this.getHealth().getExhaustion().setMultiplier(this, getAmount() / 2 + 1);
        if(getAmount() >= 1) {
            if(once((int) (2.0F * 60 * 20 / getAmount()))) {
                this.getHealth().addMobEffect(MobEffects.CONFUSION, 0, 10 * 20);
            }
            if(once((int) (2.25F * 60 * 20 / getAmount()))) {
                this.getHealth().addMobEffect(MobEffects.BLINDNESS, 0, 5 * 20);
            }
        }
    }

    @Override
    public void sleep(int sumTicks) {
        this.addAmount(-1.0F / 1000 * sumTicks);
    }

    @Override
    protected float maxValue() {
        return 5.0F;
    }
}
