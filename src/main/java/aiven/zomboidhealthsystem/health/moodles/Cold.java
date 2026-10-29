package aiven.zomboidhealthsystem.health.moodles;

import aiven.zomboidhealthsystem.Config;
import aiven.zomboidhealthsystem.ModDamageTypes;
import aiven.zomboidhealthsystem.health.Health;
import aiven.zomboidhealthsystem.utility.Util;

import net.minecraft.world.effect.MobEffects;

public final class Cold extends SimpleMoodle {

    public Cold(Health health){
        super(health);
    }

    @Override
    public String getId() {
        return "cold";
    }

    @Override
    public void update() {
        float amplifier = (Temperature.AVERAGE_TEMPERATURE_BODY - getHealth().getTemperature().getAmount()) / 1.5F;

        if (amplifier >= 1.0F) {
            if(Util.random(Config.getInstance().cold_chance * Health.UPDATE_FREQUENCY * amplifier * getMultiplier())) {
                this.addAmount(1.0F);
            }
        }

        getHealth().getExhaustion().setMultiplier(this, (getAmount() / 2) + 1);
        getHealth().getHunger().setAppetite(Hunger.DEFAULT_APPETITE / (getAmount() / 4 + 1));

        if(getAmount() > 1.0F) {
            if(Util.random(1.0F / (4 * 60 * 20) * Health.UPDATE_FREQUENCY * getAmount())) {
                getHealth().addMobEffect(MobEffects.CONFUSION, 0, 10 * 20);
            }
            if(getAmount() >= 2.0F) {
                getHealth().addMobEffect(MobEffects.MOVEMENT_SLOWDOWN, (int)(getAmplifier() / 3.0F), 15 * 20);
                if (getAmount() >= 4.0F) {
                    getHealth().die(Util.getDamageSource(ModDamageTypes.COLD, getPlayer().level()));
                }
            }
        }
    }

    @Override
    public void sleep(int sumTicks) {
        if(sumTicks >= 8000) {
            float amplifier = (Temperature.AVERAGE_TEMPERATURE_BODY - getHealth().getTemperature().getAmount()) / 1.5F;
            if (amplifier < 1) {
                this.addAmount(-1.0F);
            } else {
                this.addAmount(amplifier);
            }
        }
    }
}
