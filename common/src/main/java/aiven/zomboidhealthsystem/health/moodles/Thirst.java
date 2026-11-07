package aiven.zomboidhealthsystem.health.moodles;

import aiven.zomboidhealthsystem.Config;
import aiven.zomboidhealthsystem.ModDamageTypes;
import aiven.zomboidhealthsystem.health.Health;
import aiven.zomboidhealthsystem.utility.Util;

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
        return 7.1F;
    }

    @Override
    public boolean canPlayerLive() {
        return getValue() != maxValue();
    }

    @Override
    protected void _update(float tickDelta) {
        if(tickDelta != 0 && Config.getInstance().thirst_speed_multiplier > 0) {
            this.addValue(1.0F / 10000.0F * getMultiplier() * Config.getInstance().thirst_speed_multiplier * tickDelta);

            this.getHealth().getExhaustion().setMultiplier(this, 1.0F);

            if (value >= 2) {
                this.getHealth().getExhaustion().setMultiplier(this, 1 + ((getValue() - 1) / 3));
                if (value >= 3.0F) {
                    this.getHealth().addMobEffect(MobEffects.MOVEMENT_SLOWDOWN, (int) (value / 2.0F) - 1, 15 * 20);
                    this.getHealth().addMobEffect(MobEffects.DIG_SLOWDOWN, (int) (value / 2.0F) - 1, 15 * 20);

                    if (Util.random(1.0F / (2 * 60 * 20))) {
                        this.getHealth().addMobEffect(MobEffects.CONFUSION, 0, 10 * 20);
                    }
                    if (Util.random(1.0F / (2 * 60 * 20))) {
                        this.getHealth().addMobEffect(MobEffects.BLINDNESS, 0, 10 * 20);
                    }

                    if (value >= 4) {
                        if (Util.random(1.0F / (3 * 60 * 20))) {
                            this.getHealth().addMobEffect(MobEffects.BLINDNESS, 0, 5 * 20);
                        }
                        if (Util.random(1.0F / (3 * 60 * 20))) {
                            this.getHealth().addMobEffect(MobEffects.CONFUSION, 0, 10 * 20);
                        }
                    }
                }
            }

            if (value >= maxValue() - 0.2F) {
                this.getHealth().addMobEffect(MobEffects.BLINDNESS, 0, 15 * 20);
            }
        }

        if(Config.getInstance().thirst_speed_multiplier == 0) {
            setValue(defaultValue());
        }
    }

    @Override
    public void sleep(int ticks) {
        this.setValue(this.getValue() + (0.35F / 8000) * ticks * Config.getInstance().thirst_speed_multiplier);
    }

    @Override
    public void onSleep(int sumTicks) {

    }

    @Override
    protected ResourceKey<DamageType> getDamageType() {
        return ModDamageTypes.THIRST;
    }

    public void drink(float amount, boolean clean) {
        if (clean) {
            this.addValue(-amount / 2);
        }
        if(amount >= 1.0F) {
            this.getHealth().getTemperature().cool(0.33F);
        }
    }
}
