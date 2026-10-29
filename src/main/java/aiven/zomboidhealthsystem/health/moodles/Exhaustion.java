package aiven.zomboidhealthsystem.health.moodles;

import aiven.zomboidhealthsystem.Config;
import aiven.zomboidhealthsystem.health.Health;

import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Pose;

public final class Exhaustion extends SimpleMoodle {

    public Exhaustion(Health health) {
        super(health);
    }

    @Override
    public String getId() {
        return "exhaustion";
    }

    @Override
    public void update() {
        getHealth().getThirst().setMultiplier(this, getValue() / 1.75F + 1.0F);
        getHealth().getHunger().setMultiplier(this, getValue() / 2.0F + 1.0F);
        getHealth().getDrowsiness().setMultiplier(this, getValue() + 1.0F);

        if (this.getPlayer().isSprinting()) {
            this.addAmount(1.0F / 1100 * getMultiplier() * Config.getInstance().exhaustion_multiplier  * Health.UPDATE_FREQUENCY);
        } else {
            this.addAmount(-1.0F / 1100 * (isFasterRest() ? 1.25F : 1.0F) * Config.getInstance().exhaustion_multiplier * Health.UPDATE_FREQUENCY);
        }

        if (value >= 1.5F) {
            this.getHealth().addMobEffect(MobEffects.MOVEMENT_SLOWDOWN, (int) value - 1, 20 * 5);
            this.getHealth().addMobEffect(MobEffects.DIG_SLOWDOWN, (int) value - 1, 20 * 5);
        }

        Temperature temperature = getHealth().getTemperature();
        temperature.addHeat(getValue() * 3);
    }

    @Override
    public void sleep(int sumTicks) {
        this.addAmount(-1.0F / 400 * sumTicks);
    }

    public void onJump() {
        this.addAmount(Config.getInstance().exhaustion_per_jump * this.getMultiplier() * Config.getInstance().exhaustion_multiplier);
    }

    private boolean isFasterRest() {
        return getPlayer().getPose().equals(Pose.SWIMMING) || getPlayer().getPose().equals(Pose.SLEEPING) || getPlayer().getPose().equals(Pose.SITTING) || getPlayer().getPose().equals(Pose.CROUCHING);
    }
}
