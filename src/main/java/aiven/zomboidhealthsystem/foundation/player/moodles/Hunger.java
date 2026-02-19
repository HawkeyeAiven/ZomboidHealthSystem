package aiven.zomboidhealthsystem.foundation.player.moodles;

import aiven.zomboidhealthsystem.Config;
import aiven.zomboidhealthsystem.ModDamageTypes;
import aiven.zomboidhealthsystem.foundation.player.Health;
import aiven.zomboidhealthsystem.foundation.utility.Util;
import net.minecraft.entity.effect.StatusEffects;

public class Hunger extends Moodle {
    public static final float DEFAULT_APPETITE = 0.14F;

    private final float min_amount = -1.0F;
    private float appetite = DEFAULT_APPETITE;

    public Hunger(Health health) {
        super(health);
    }

    @Override
    public void update() {
        this.addAmount(1.0F / 15000 * getMultiplier() * (this.getAmount() < 0 ? 2.25F : 1.0F) * Config.HUNGER_MULTIPLIER.getValue() * Health.UPDATE_FREQUENCY);

        if(amount >= 1.0F) {
            this.getHealth().getExhaustion().addMultiplier(this, (getAmount() / 3) + 1);
            if(amount >= 2.5F) {
                if(once(5 * 60 * 20)) {
                    getHealth().addStatusEffect(StatusEffects.NAUSEA, 0, 3 * 20);
                }
                if(amount >= 4.0F) {
                    getHealth().addStatusEffect(StatusEffects.SLOWNESS, (int)(amount / 3) - 1, 15 * 20);
                    if(amount >= 6.0F) {
                        getHealth().addStatusEffect(StatusEffects.MINING_FATIGUE,(int) (amount / 4) - 1, 15 * 20);
                        if(once(5 * 60 * 20)) {
                            getHealth().addStatusEffect(StatusEffects.NAUSEA, 0, 3 * 20);
                        }
                        if(once(5 * 60 * 20)) {
                            getHealth().addStatusEffect(StatusEffects.DARKNESS, 0, 3 * 20);
                        }
                        if (once(5 * 60 * 20)) {
                            getHealth().stumble(0);
                        }
                        if(amount >= 8.4F) {
                            if(once(3 * 60 * 20)) {
                                getHealth().addStatusEffect(StatusEffects.NAUSEA, 0, 3 * 20);
                            }
                            if(once(3 * 60 * 20)) {
                                getHealth().addStatusEffect(StatusEffects.DARKNESS, 0, 3 * 20);
                            }
                            if (once(3 * 60 * 20)) {
                                getHealth().stumble(0);
                            }
                            if (amount >= 10.5F) {
                                this.getHealth().onDeath(Util.getDamageSource(ModDamageTypes.HUNGER, getPlayer().getWorld()));
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onSleep() {
        super.onSleep();
        this.addAmount(0.4F);
    }

    @Override
    public int getAmplifier() {
        if(getAmount() > 0) {
            return super.getAmplifier();
        } else {
            return (int) (getAmount() * 4);
        }
    }

    public void eatFood(int hunger, float saturationModifier) {
        this.addAmount((float)-hunger * getAppetite());
        this.getHealth().healPlayerHp(Config.FOOD_HEAL_AMOUNT.getValue() * hunger);
    }

    public boolean canEat() {
        return getAmount() >= min_amount + 0.35F;
    }

    public float getAppetite() {
        return appetite;
    }

    public void setAppetite(float appetite) {
        this.appetite = appetite;
    }

    @Override
    public void setAmount(float amount) {
        this.amount = Math.max(amount, min_amount);
    }

    @Override
    public String getId() {
        return "hunger";
    }

    @Override
    public void readNbt(String value) {
        super.readNbt(value);
    }

    @Override
    public String getNbt() {
        return super.getNbt();
    }
}
