package aiven.zomboidhealthsystem.health.moodles;

import aiven.zomboidhealthsystem.Config;
import aiven.zomboidhealthsystem.ModDamageTypes;
import aiven.zomboidhealthsystem.health.Health;
import aiven.zomboidhealthsystem.utility.Util;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffects;

public final class Cold extends ImportantMoodle {

    public Cold(Health health){
        super(health);
    }

    @Override
    public String getId() {
        return "cold";
    }

    @Override
    public float maxValue() {
        return 4.0F;
    }

    @Override
    public boolean canPlayerLive() {
        return getValue() != maxValue();
    }

    @Override
    protected void _update() {
        float amplifier = (Temperature.AVERAGE_TEMPERATURE_BODY - getHealth().getTemperature().getValue()) / 1.5F;

        if (amplifier >= 1.0F) {
            if(Util.random(Config.getInstance().cold_chance * Health.UPDATE_FREQUENCY * amplifier * getMultiplier())) {
                this.addValue(1.0F);
            }
        }

        getHealth().getExhaustion().setMultiplier(this, (getValue() / 2) + 1);
        getHealth().getHunger().setAppetite(Hunger.DEFAULT_APPETITE / (getValue() / 4 + 1));

        if(getValue() > 1.0F) {
            if(Util.random(1.0F / (4 * 60 * 20) * Health.UPDATE_FREQUENCY * getValue())) {
                getHealth().addMobEffect(MobEffects.CONFUSION, 0, 10 * 20);
            }
            if(getValue() >= 2.0F) {
                getHealth().addMobEffect(MobEffects.MOVEMENT_SLOWDOWN, (int)(getAmplifier() / 3.0F), 15 * 20);
            }
        }
    }

    @Override
    public void sleep(int sumTicks) {
        if(sumTicks >= 8000) {
            float amplifier = (Temperature.AVERAGE_TEMPERATURE_BODY - getHealth().getTemperature().getValue()) / 1.5F;
            if (amplifier < 1) {
                this.addValue(-1.0F);
            } else {
                this.addValue(amplifier);
            }
        }
    }

    @Override
    protected ResourceKey<DamageType> getDamageType() {
        return ModDamageTypes.COLD;
    }
}
