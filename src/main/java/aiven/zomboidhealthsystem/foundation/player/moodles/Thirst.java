package aiven.zomboidhealthsystem.foundation.player.moodles;

import aiven.zomboidhealthsystem.Config;
import aiven.zomboidhealthsystem.ModDamageTypes;
import aiven.zomboidhealthsystem.foundation.player.Health;
import aiven.zomboidhealthsystem.foundation.utility.Util;
import net.minecraft.entity.effect.StatusEffects;

public class Thirst extends Moodle {

    public Thirst(Health health) {
        super(health);
    }

    @Override
    public String getId() {
        return "thirst";
    }

    public void setAmount(float amount) {
        this.amount = Math.max(0, amount);
    }

    @Override
    public void update() {
        this.addAmount(1.0F / 15000.0F * getMultiplier() * Config.THIRST_MULTIPLIER.getValue() * Health.UPDATE_FREQUENCY);

        this.getHealth().getExhaustion().addMultiplier(this, 1.0F);

        if (amount >= 1) {
            this.getHealth().getExhaustion().addMultiplier(this, getAmount());
            if (amount >= 2) {

                if (once(4 * 60 * 20)) {
                    this.getHealth().addStatusEffect(StatusEffects.BLINDNESS, 0, 10 * 20);
                }

                if (amount >= 2.4F) {
                    this.getHealth().addStatusEffect(StatusEffects.SLOWNESS, (int) (amount - 1.6F), 15 * 20);
                    if (once(2 * 60 * 20)) {
                        this.getHealth().stumble(0);
                    }

                    if (amount >= 2.8F) {
                        this.getHealth().addStatusEffect(StatusEffects.BLINDNESS, 0, 15 * 20);
                        if (once(60 * 20)) {
                            this.getHealth().stumble(0);
                        }

                        if (amount >= 3.1F) {
                            this.getHealth().onDeath(Util.getDamageSource(ModDamageTypes.THIRST, getPlayer().getWorld()));
                        }
                    }
                }
            }
        }
    }

    public void drink(float amount, boolean clean) {
        if (clean) {
            this.addAmount(-amount);
        }
        this.getHealth().getTemperature().cool(0.5F);
    }

    @Override
    public void sleep(int ticks) {
        this.setAmount(this.getAmount() + 0.35F / 8000 * ticks);
    }
}
