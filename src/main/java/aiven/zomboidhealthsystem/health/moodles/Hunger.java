package aiven.zomboidhealthsystem.health.moodles;

import aiven.zomboidhealthsystem.Config;
import aiven.zomboidhealthsystem.ModDamageTypes;
import aiven.zomboidhealthsystem.health.Health;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffects;

public final class Hunger extends ImportantMoodle {
    public static final float DEFAULT_APPETITE = 0.14F;

    private float appetite = DEFAULT_APPETITE;

    public Hunger(Health health) {
        super(health);
    }

    @Override
    public String getId() {
        return "hunger";
    }

    @Override
    public float maxValue() {
        return 10.5F;
    }

    @Override
    public boolean canPlayerLive() {
        return getValue() != maxValue();
    }

    @Override
    public float minValue() {
        return -1.0F;
    }

    @Override
    protected void _update() {
        this.addValue(1.0F / 10000 * getMultiplier() * (this.getValue() < 0 ? 2.25F : 1.0F) * Config.getInstance().hunger_multiplier * Health.UPDATE_FREQUENCY);

        if(value >= 1.0F) {
            this.getHealth().getExhaustion().setMultiplier(this, (getValue() / 3) + 1);
            if(value >= 2.5F) {
                if(once(5 * 60 * 20)) {
                    getHealth().addMobEffect(MobEffects.CONFUSION, 0, 10 * 20);
                }
                if(value >= 4.0F) {
                    getHealth().addMobEffect(MobEffects.MOVEMENT_SLOWDOWN, (int)(value / 3) - 1, 15 * 20);
                    if(value >= 6.0F) {
                        getHealth().addMobEffect(MobEffects.DIG_SLOWDOWN,(int) (value / 4) - 1, 15 * 20);
                        if(once(5 * 60 * 20)) {
                            getHealth().addMobEffect(MobEffects.CONFUSION, 0, 10 * 20);
                        }
                        if(once(5 * 60 * 20)) {
                            getHealth().addMobEffect(MobEffects.DARKNESS, 0, 5 * 20);
                        }
                        if(value >= 8.4F) {
                            if(once(3 * 60 * 20)) {
                                getHealth().addMobEffect(MobEffects.CONFUSION, 0, 15 * 20);
                            }
                            if(once(3 * 60 * 20)) {
                                getHealth().addMobEffect(MobEffects.DARKNESS, 0, 10 * 20);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public int getAmplifier() {
        if(getValue() > 0) {
            return super.getAmplifier();
        } else {
            return (int) (getValue() * 4);
        }
    }

    @Override
    public void sleep(int sumTicks) {
        this.addValue(0.5F / 8000 * sumTicks * (getValue() < 0 ? 2.25F : 1.0F));
    }

    @Override
    protected ResourceKey<DamageType> getDamageType() {
        return ModDamageTypes.HUNGER;
    }

    @Override
    public String getMoodleIconText() {
        if(getValue() > 0) {
            return super.getMoodleIconText();
        } else {
            return Component.translatable("zomboid-health-system.moodle.saturation").getString() + " " + (-getAmplifier());
        }
    }

    public void eatFood(int hunger, float saturationModifier) {
        if(!getLevel().isClientSide) {
            this.addValue((float) -hunger * getAppetite());
            this.health.getBlood().addValue(Config.getInstance().food_heal_amount * hunger);
        }
    }

    public boolean canEat() {
        return getValue() >= minValue() + 0.35F;
    }

    public float getAppetite() {
        return appetite;
    }

    public void setAppetite(float appetite) {
        this.appetite = appetite;
    }
}
