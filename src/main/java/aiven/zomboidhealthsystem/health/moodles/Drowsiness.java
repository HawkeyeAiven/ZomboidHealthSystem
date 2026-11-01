package aiven.zomboidhealthsystem.health.moodles;

import aiven.zomboidhealthsystem.Config;
import aiven.zomboidhealthsystem.ModDamageTypes;
import aiven.zomboidhealthsystem.ZomboidHealthSystem;
import aiven.zomboidhealthsystem.health.Health;
import aiven.zomboidhealthsystem.utility.Util;

import de.marhali.json5.Json5;
import de.marhali.json5.Json5Object;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffects;

import java.io.IOException;


public final class Drowsiness extends ImportantMoodle {
    private static final Json5 mapper = new Json5();

    private int drowsinessTime;

    private float caffeine_value = 0;
    private float max_caffeine_value = 0;
    private boolean caffeine_effect = true;

    private float sleeping_pills_value = 0;
    private float max_sleeping_pills_value = 0;
    private boolean sleeping_pills_effect = true;

    public Drowsiness(Health health) {
        super(health);
        if(getPlayer() != null && !getPlayer().level().isClientSide()) {
            drowsinessTime = 24000;
        }
    }

    @Override
    public String getId() {
        return "drowsiness";
    }

    @Override
    public boolean canPlayerLive() {
        return getAmplifier() < 5;
    }

    @Override
    protected void _update(float tickDelta) {
        if(tickDelta != 0) {
            addTicks(1.0F * 1.58F * getMultiplier() * Config.getInstance().drowsiness_multiplier * tickDelta);

            if (caffeine_effect) {
                if (caffeine_value < max_caffeine_value) {
                    caffeine_value += 1.0F / (60 * 20) * tickDelta;
                } else if (max_caffeine_value != 0) {
                    caffeine_effect = false;
                }
            } else {
                if (caffeine_value > max_caffeine_value * -0.75F) {
                    caffeine_value -= 1.0F / (drowsinessTime / 4.0F) * tickDelta;
                }
            }

            if (sleeping_pills_effect) {
                if (sleeping_pills_value < max_sleeping_pills_value) {
                    sleeping_pills_value += 1.0F / (60 * 20) * Config.getInstance().sleeping_pills_multiplier * tickDelta;
                } else if (max_sleeping_pills_value != 0) {
                    sleeping_pills_effect = false;
                }
            } else {
                if (sleeping_pills_value > 0) {
                    sleeping_pills_value -= 1.0F / (5 * 60 * 20) * tickDelta;
                } else {
                    sleeping_pills_value = 0;
                    max_sleeping_pills_value = 0;
                    sleeping_pills_effect = true;
                }
            }

            this.getHealth().getExhaustion().setMultiplier(this, ((float) getAmplifier() / 2) + 1);

            if (this.getAmplifier() >= 1) {
                if (Util.random(1.0F / ((float) (5 * 60 * 20) / this.getAmplifier()))) {
                    this.getHealth().addMobEffect(MobEffects.BLINDNESS, 0, 5 * 20);
                }

                if (this.getAmplifier() >= 2) {
                    if (this.getAmplifier() >= 3) {
                        this.getHealth().addMobEffect(MobEffects.DIG_SLOWDOWN, (this.getAmplifier() / 2) - 1, 15 * 20);
                    }
                }
            }
        }

        if(getPlayer().isSleeping()) {
            if (getAmplifier() < Config.getInstance().min_drowsiness_for_sleep) {
                getPlayer().stopSleeping();
                getPlayer().sendSystemMessage(Component.translatable(ZomboidHealthSystem.ID + ".message.dont_want_sleep"));
            }
        }
    }

    @Override
    public void sleep(int sumTicks) {
        if(sumTicks >= 1000) {
            this.max_caffeine_value = 0;
            this.caffeine_value = 0;
            this.caffeine_effect = true;
            this.sleeping_pills_value = 0;
            this.max_sleeping_pills_value = 0;
            this.sleeping_pills_effect = true;
        }
        this.value = Math.max(0, value - (float) sumTicks / 8000.0F);
    }

    @Override
    public boolean showIcon() {
        return this.getAmplifier() >= 1;
    }

    @Override
    public String getNbt() {
        if(this.getValue() != 0 || this.caffeine_value != 0 || this.max_caffeine_value != 0) {
            Json5Object object = new Json5Object();

            object.addProperty("amount", this.getValue());
            object.addProperty("caffeine_value", this.caffeine_value);
            object.addProperty("max_caffeine_value", this.max_caffeine_value);
            object.addProperty("caffeine_effect", this.caffeine_effect);
            object.addProperty("sleeping_pills_value", this.sleeping_pills_value);
            object.addProperty("max_sleeping_pills_value", this.max_sleeping_pills_value);
            object.addProperty("sleeping_pills_effect", this.sleeping_pills_effect);

            try {
                return mapper.serialize(object);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    @Override
    public void readNbt(String value) {
        if(value == null) {
            this.caffeine_value = 0;
            this.max_caffeine_value = 0;
            this.sleeping_pills_value = 0;
            this.max_sleeping_pills_value = 0;
            this.value = 0;
            this.sleeping_pills_effect = true;
            this.caffeine_effect = true;
        } else {
            Json5Object object = mapper.parse(value).getAsJson5Object();
            this.value = object.get("amount").getAsFloat();
            this.caffeine_value = object.get("caffeine_value").getAsFloat();
            this.max_caffeine_value = object.get("max_caffeine_value").getAsFloat();
            this.caffeine_effect = object.get("caffeine_effect").getAsBoolean();
            this.sleeping_pills_value = object.get("sleeping_pills_value").getAsFloat();
            this.max_sleeping_pills_value = object.get("max_sleeping_pills_value").getAsFloat();
            this.sleeping_pills_effect = object.get("sleeping_pills_effect").getAsBoolean();
        }
    }

    @Override
    protected ResourceKey<DamageType> getDamageType() {
        return ModDamageTypes.DROWSINESS;
    }

    @Override
    public int getAmplifier() {
        return (int) (value - caffeine_value + sleeping_pills_value);
    }

    public void addCaffeine(float amount) {
        this.max_caffeine_value += amount;
        this.caffeine_effect = true;
    }

    public void addSleepingPills(float amount) {
        this.max_sleeping_pills_value += amount;
        this.sleeping_pills_effect = true;
    }

    public void addTicks(float amount) {
        addValue(amount / drowsinessTime);
    }
}