package aiven.zomboidhealthsystem.foundation.player.moodles;

import aiven.zomboidhealthsystem.Config;
import aiven.zomboidhealthsystem.ModDamageTypes;
import aiven.zomboidhealthsystem.foundation.player.Health;
import aiven.zomboidhealthsystem.foundation.utility.Util;
import net.minecraft.entity.effect.StatusEffects;


public class Cold extends Moodle {
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
            if(Util.random(Config.COLD_CHANCE.getValue() * Health.UPDATE_FREQUENCY * amplifier * getMultiplier())) {
                this.addAmount(1.0F);
            }
        }

        getHealth().getExhaustion().addMultiplier(this, (getAmount() / 2) + 1);
        getHealth().getHunger().setAppetite(Hunger.DEFAULT_APPETITE / (getAmount() / 4 + 1));

        if(getAmount() > 1.0F) {
            if(Util.random(1.0F / (4 * 60 * 20) * Health.UPDATE_FREQUENCY * getAmount())) {
                getHealth().addStatusEffect(StatusEffects.NAUSEA, 0, 5 * 20);
            }
            if(getAmount() >= 2.0F) {
                getHealth().addStatusEffect(StatusEffects.SLOWNESS, (int)(getAmplifier() / 3.0F), 15 * 20);
                if (getAmount() >= 4.0F) {
                    getHealth().onDeath(Util.getDamageSource(ModDamageTypes.COLD, getPlayer().getWorld()));
                }
            }
        }
    }

    @Override
    public void onSleep(){
        float amplifier = (Temperature.AVERAGE_TEMPERATURE_BODY - getHealth().getTemperature().getAmount()) / 1.5F;
        if(amplifier < 1) {
            this.addAmount(-1.0F);
        } else {
            this.addAmount(amplifier);
        }
    }

    @Override
    public void setAmount(float amount) {
        this.amount = Math.max(amount, 0);
    }
}
