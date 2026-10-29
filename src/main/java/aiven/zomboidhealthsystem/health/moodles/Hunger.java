package aiven.zomboidhealthsystem.health.moodles;

import aiven.zomboidhealthsystem.Config;
import aiven.zomboidhealthsystem.ModDamageTypes;
import aiven.zomboidhealthsystem.health.Health;
import aiven.zomboidhealthsystem.utility.Util;

import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffects;

public final class Hunger extends SimpleMoodle {
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
    protected float minValue() {
        return -1.0F;
    }

    @Override
    public void update() {
        this.addAmount(1.0F / 15000 * getMultiplier() * (this.getAmount() < 0 ? 2.25F : 1.0F) * Config.getInstance().hunger_multiplier * Health.UPDATE_FREQUENCY);

        if(amount >= 1.0F) {
            this.getHealth().getExhaustion().setMultiplier(this, (getAmount() / 3) + 1);
            if(amount >= 2.5F) {
                if(once(5 * 60 * 20)) {
                    getHealth().addMobEffect(MobEffects.CONFUSION, 0, 10 * 20);
                }
                if(amount >= 4.0F) {
                    getHealth().addMobEffect(MobEffects.MOVEMENT_SLOWDOWN, (int)(amount / 3) - 1, 15 * 20);
                    if(amount >= 6.0F) {
                        getHealth().addMobEffect(MobEffects.DIG_SLOWDOWN,(int) (amount / 4) - 1, 15 * 20);
                        if(once(5 * 60 * 20)) {
                            getHealth().addMobEffect(MobEffects.CONFUSION, 0, 10 * 20);
                        }
                        if(once(5 * 60 * 20)) {
                            getHealth().addMobEffect(MobEffects.DARKNESS, 0, 5 * 20);
                        }
                        if(amount >= 8.4F) {
                            if(once(3 * 60 * 20)) {
                                getHealth().addMobEffect(MobEffects.CONFUSION, 0, 15 * 20);
                            }
                            if(once(3 * 60 * 20)) {
                                getHealth().addMobEffect(MobEffects.DARKNESS, 0, 10 * 20);
                            }
                            if (amount >= 10.5F) {
                                this.getHealth().die(Util.getDamageSource(ModDamageTypes.HUNGER, getPlayer().level()));
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public int getAmplifier() {
        if(getAmount() > 0) {
            return super.getAmplifier();
        } else {
            return (int) (getAmount() * 4);
        }
    }

    @Override
    public void sleep(int sumTicks) {
        this.addAmount(0.5F / 8000 * sumTicks * (getAmount() < 0 ? 2.25F : 1.0F));
    }

    @Override
    public String getMoodleIconText() {
        if(getAmount() > 0) {
            return super.getMoodleIconText();
        } else {
            return Component.translatable("zomboid-health-system.moodle.saturation").getString() + " " + (-getAmplifier());
        }
    }

    public void eatFood(int hunger, float saturationModifier) {
        if(!getLevel().isClientSide) {
            this.addAmount((float) -hunger * getAppetite());
            this.getHealth().healPlayerHp(Config.getInstance().food_heal_amount * hunger);
        }
    }

    public boolean canEat() {
        return getAmount() >= minValue() + 0.35F;
    }

    public float getAppetite() {
        return appetite;
    }

    public void setAppetite(float appetite) {
        this.appetite = appetite;
    }
}
