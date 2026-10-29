package aiven.zomboidhealthsystem.health.moodles;

import aiven.zomboidhealthsystem.Config;
import aiven.zomboidhealthsystem.ModDamageTypes;
import aiven.zomboidhealthsystem.health.Health;
import aiven.zomboidhealthsystem.utility.Util;

import net.minecraft.world.effect.MobEffects;

public final class Thirst extends SimpleMoodle {

    public Thirst(Health health) {
        super(health);
    }

    @Override
    public String getId() {
        return "thirst";
    }

    @Override
    public void update() {
        this.addAmount(1.0F / 15000.0F * getMultiplier() * Config.getInstance().thirst_multiplier * Health.UPDATE_FREQUENCY);

        this.getHealth().getExhaustion().setMultiplier(this, 1.0F);

        if (amount >= 1) {
            this.getHealth().getExhaustion().setMultiplier(this, getAmount());
            if (amount >= 2) {
                if (once(4 * 60 * 20)) {
                    this.getHealth().addMobEffect(MobEffects.BLINDNESS, 0, 5 * 20);
                }
                if (once(4 * 60 * 20)) {
                    this.getHealth().addMobEffect(MobEffects.CONFUSION, 0, 10 * 20);
                }

                if (amount >= 2.4F) {
                    this.getHealth().addMobEffect(MobEffects.MOVEMENT_SLOWDOWN, (int) (amount - 1.6F), 15 * 20);
                    if (once(2 * 60 * 20)) {
                        this.getHealth().addMobEffect(MobEffects.CONFUSION, 0, 10 * 20);
                    }

                    if (amount >= 2.8F) {
                        this.getHealth().addMobEffect(MobEffects.BLINDNESS, 0, 15 * 20);

                        if (amount >= 3.1F) {
                            this.getHealth().die(Util.getDamageSource(ModDamageTypes.THIRST, getPlayer().level()));
                        }
                    }
                }
            }
        }
    }

    @Override
    public void sleep(int sumTicks) {
        this.setAmount(this.getAmount() + 0.35F / 8000 * sumTicks);
    }

    public void drink(float amount, boolean clean) {
        if (clean) {
            this.addAmount(-amount);
        }
        this.getHealth().getTemperature().cool(0.5F);
    }
}
