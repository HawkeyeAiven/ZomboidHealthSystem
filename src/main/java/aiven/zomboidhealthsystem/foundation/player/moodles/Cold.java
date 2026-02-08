package aiven.zomboidhealthsystem.foundation.player.moodles;

import aiven.zomboidhealthsystem.Config;
import aiven.zomboidhealthsystem.ModDamageTypes;
import aiven.zomboidhealthsystem.ModStatusEffects;
import aiven.zomboidhealthsystem.foundation.player.Health;
import aiven.zomboidhealthsystem.foundation.utility.Util;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;


public class Cold extends Moodle {
    private final DamageSource source;

    public Cold(Health health){
        super(health);
        source = Util.getDamageSource(ModDamageTypes.COLD, getPlayer().getWorld());
    }

    @Override
    StatusEffect getEffect() {
        return ModStatusEffects.COLD;
    }

    @Override
    public String getId() {
        return "cold";
    }

    @Override
    public void setAmount(float amount) {
        if(amount > 0) {
            this.amount = amount;
        } else {
            this.amount = 0;
        }
    }

    @Override
    public void update() {
        super.update();
        float bodyTemperature = getHealth().getTemperature().getAmount();
        float wet = (getHealth().getWet().getAmount() / 2.0F) + 1.0F;
        float d = Math.max((float) Math.sqrt(35.0F - bodyTemperature),1);

        if (bodyTemperature < 35.0F && getAmount() < 1.0F) {
            this.addAmount(1.0F / (5 * 60 * 20) * d * wet * Config.COLD_MULTIPLIER.getValue() * Health.UPDATE_FREQUENCY);
        }

        if(getAmount() >= 1.0F){
            if(bodyTemperature < 35.0F) {
                this.addAmount(1.0F / (25 * 60 * 20) * d * wet * Config.COLD_MULTIPLIER.getValue() * Health.UPDATE_FREQUENCY);
            }

            this.getHealth().getExhaustion().addMultiplier(getAmount());
            this.getHealth().getDrowsiness().addTicks(getAmount() / 2 * Health.UPDATE_FREQUENCY);
            this.getHealth().getThirst().addAmount(getAmount() / (20 * 60 * 20));

            if(getAmount() >= 2.5F){
                if(random(60 * 20)){
                    this.getHealth().addStatusEffect(StatusEffects.NAUSEA,1, 10 * 20);
                }

                if(getAmount() >= 3.1F) {
                    this.getHealth().onDeath(source);
                }
            }
        }
    }

    @Override
    public void onSleep(){
        Temperature temperature = getHealth().getTemperature();
        Wet wet = getHealth().getWet();
        float multiplier = (wet.getAmount() / 2) + 1;
        if (temperature.getAmount() < 35.0F) {
            this.addAmount(0.75F * multiplier);
        } else if(temperature.getAmount() > 36.0F){
            this.addAmount(-0.5F / multiplier);
        }
    }
}
