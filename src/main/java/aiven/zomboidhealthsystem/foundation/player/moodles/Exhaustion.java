package aiven.zomboidhealthsystem.foundation.player.moodles;

import aiven.zomboidhealthsystem.Config;
import aiven.zomboidhealthsystem.ModStatusEffects;
import aiven.zomboidhealthsystem.foundation.player.Health;
import aiven.zomboidhealthsystem.foundation.utility.Util;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;

import java.util.ArrayList;

public class Exhaustion extends Moodle {
    private final ArrayList<Float> amplifiers = new ArrayList<>();

    public Exhaustion(Health health) {
        super(health);
    }

    @Override
    StatusEffect getEffect() {
        return ModStatusEffects.EXHAUSTION;
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
        if (this.getPlayer().isSprinting()) {
            this.addAmount(1.0F / 1000 * Config.EXHAUSTION_MULTIPLIER.getValue() * this.getSumAmplifiers() * Health.UPDATE_FREQUENCY);
        } else {
            this.addAmount(-1.0F / 1000 * Config.EXHAUSTION_MULTIPLIER.getValue() * Health.UPDATE_FREQUENCY);
        }

        if (amount > 1.5F) {
            getHealth().getWet().addAmount(1 / 500F * (Util.getArmorCount(getPlayer()) >= 3 ? 2 : 1) * Health.UPDATE_FREQUENCY);
            this.getHealth().addStatusEffect(StatusEffects.SLOWNESS, (int) amount, 20 * 5);
            this.getHealth().addStatusEffect(StatusEffects.MINING_FATIGUE, (int) amount, 20 * 5);
        }

        Temperature temperature = getHealth().getTemperature();
        temperature.addHeat(getAmount() * 3);

        this.amplifiers.clear();
    }

    @Override
    public void onSleep() {
        this.setAmount(0);
        this.amplifiers.clear();
    }

    public void addMultiplier(Float multiplier) {
        if (!amplifiers.contains(multiplier)) {
            amplifiers.add(multiplier);
        }
    }

    public float getSumAmplifiers() {
        float sum = 1;
        for (float f : amplifiers) {
            sum += f - 1;
        }
        return sum;
    }
}
