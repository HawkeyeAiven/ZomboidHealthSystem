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
        getHealth().getThirst().setMultiplier(this, getAmount() / 1.75F + 1.0F);
        getHealth().getHunger().setMultiplier(this, getAmount() / 2.0F + 1.0F);
        getHealth().getDrowsiness().setMultiplier(this, getAmount() + 1.0F);

        if (this.getPlayer().isSprinting()) {
            this.addAmount(1.0F / 1100 * getMultiplier() * Config.getInstance().exhaustion_multiplier  * Health.UPDATE_FREQUENCY);
        } else {
            this.addAmount(-1.0F / 1100 * (isFasterRest() ? 1.25F : 1.0F) * Config.getInstance().exhaustion_multiplier * Health.UPDATE_FREQUENCY);
        }

        if (amount >= 1.5F) {
            this.getHealth().addMobEffect(MobEffects.MOVEMENT_SLOWDOWN, (int) amount - 1, 20 * 5);
            this.getHealth().addMobEffect(MobEffects.DIG_SLOWDOWN, (int) amount - 1, 20 * 5);
        }

        Temperature temperature = getHealth().getTemperature();
        temperature.addHeat(getAmount() * 3);
    }

    @Override
    public void sleep(int sumTicks) {
        this.addAmount(-1.0F / 400 * sumTicks);
    }

    public boolean canPlayerWalk() {
        return getAmount() < 4.0F;
    }

    public void onJump() {
        this.addAmount(Config.getInstance().exhaustion_per_jump * this.getMultiplier() * Config.getInstance().exhaustion_multiplier);
    }

    private boolean isFasterRest() {
        return getPlayer().getPose().equals(Pose.SWIMMING) || getPlayer().getPose().equals(Pose.SLEEPING) || getPlayer().getPose().equals(Pose.SITTING) || getPlayer().getPose().equals(Pose.CROUCHING);
    }
}
