package aiven.zomboidhealthsystem.foundation.player.moodles;

import aiven.zomboidhealthsystem.Config;
import aiven.zomboidhealthsystem.foundation.player.Health;
import net.minecraft.entity.effect.StatusEffects;

public class Exhaustion extends Moodle {
    public Exhaustion(Health health) {
        super(health);
    }

    @Override
    public String getId() {
        return "exhaustion";
    }

    @Override
    public void setAmount(float amount) {
        this.amount = Math.max(0, amount);
    }

    @Override
    public void update() {
        super.update();

        getHealth().getThirst().addMultiplier(this, getAmount() / 1.75F + 1.0F);
        getHealth().getHunger().addMultiplier(this, getAmount() / 2.0F + 1.0F);
        getHealth().getDrowsiness().addMultiplier(this, getAmount() + 1.0F);

        if (this.getPlayer().isSprinting()) {
            this.addAmount(1.0F / 1000 * getMultiplier() * Config.EXHAUSTION_MULTIPLIER.getValue()  * Health.UPDATE_FREQUENCY);
        } else {
            this.addAmount(-1.0F / 1000 * (getPlayer().isCrawling() ? 1.25F : 1.0F) * Config.EXHAUSTION_MULTIPLIER.getValue() * Health.UPDATE_FREQUENCY);
        }

        if (amount >= 1.5F) {
            this.getHealth().addStatusEffect(StatusEffects.SLOWNESS, (int) amount - 1, 20 * 5);
            this.getHealth().addStatusEffect(StatusEffects.MINING_FATIGUE, (int) amount - 1, 20 * 5);
        }

        Temperature temperature = getHealth().getTemperature();
        temperature.addHeat(getAmount() * 3);
    }

    public boolean canPlayerWalk() {
        return getAmount() < 4.0F;
    }

    public void onJump() {
        this.addAmount(Config.EXHAUSTION_PER_JUMP.getValue() * this.getMultiplier());
    }

    @Override
    public void sleep(int ticks) {
        super.sleep(ticks);
        this.addAmount(-1.0F / 400 * ticks);
    }
}
