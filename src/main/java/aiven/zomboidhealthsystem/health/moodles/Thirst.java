package aiven.zomboidhealthsystem.health.moodles;

import aiven.zomboidhealthsystem.Config;
import aiven.zomboidhealthsystem.ModDamageTypes;
import aiven.zomboidhealthsystem.health.Health;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffects;

public final class Thirst extends ImportantMoodle {

    public Thirst(Health health) {
        super(health);
    }

    @Override
    public String getId() {
        return "thirst";
    }

    @Override
    public float maxValue() {
        return 3.1F;
    }

    @Override
    public boolean canPlayerLive() {
        return getValue() != maxValue();
    }

    @Override
    protected void _update() {
        this.addValue(1.0F / 10000.0F * getMultiplier() * Config.getInstance().thirst_multiplier * Health.UPDATE_FREQUENCY);

        this.getHealth().getExhaustion().setMultiplier(this, 1.0F);

        if (value >= 1) {
            this.getHealth().getExhaustion().setMultiplier(this, getValue());
            if (value >= 2) {
                if (once(4 * 60 * 20)) {
                    this.getHealth().addMobEffect(MobEffects.BLINDNESS, 0, 5 * 20);
                }
                if (once(4 * 60 * 20)) {
                    this.getHealth().addMobEffect(MobEffects.CONFUSION, 0, 10 * 20);
                }

                if (value >= 2.4F) {
                    this.getHealth().addMobEffect(MobEffects.MOVEMENT_SLOWDOWN, (int) (value - 1.6F), 15 * 20);
                    if (once(2 * 60 * 20)) {
                        this.getHealth().addMobEffect(MobEffects.CONFUSION, 0, 10 * 20);
                    }

                    if (value >= 2.8F) {
                        this.getHealth().addMobEffect(MobEffects.BLINDNESS, 0, 15 * 20);
                    }
                }
            }
        }
    }

    @Override
    public void sleep(int sumTicks) {
        this.setValue(this.getValue() + 0.35F / 8000 * sumTicks);
    }

    @Override
    protected ResourceKey<DamageType> getDamageType() {
        return ModDamageTypes.THIRST;
    }

    public void drink(float amount, boolean clean) {
        if (clean) {
            this.addValue(-amount);
        }
        this.getHealth().getTemperature().cool(0.5F);
    }
}
