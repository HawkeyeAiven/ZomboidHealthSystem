package aiven.zomboidhealthsystem.health.moodles;

import aiven.zomboidhealthsystem.Config;
import aiven.zomboidhealthsystem.ModDamageTypes;
import aiven.zomboidhealthsystem.ZomboidHealthSystem;
import aiven.zomboidhealthsystem.health.Health;
import aiven.zomboidhealthsystem.utility.Util;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffects;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;


public final class Drowsiness extends ImportantMoodle {
    private static final int dayLength = 24000;

    private float caffeine_value = 0;
    private float max_caffeine_value = 0;
    private boolean caffeine_effect = true;
    private float sleeping_pills_value = 0;
    private float max_sleeping_pills_value = 0;
    private boolean sleeping_pills_effect = true;

    public Drowsiness(Health health) {
        super(health);
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
        if(tickDelta != 0 && Config.getInstance().drowsiness_speed_multiplier != 0) {
            addTicks(1.0F * 1.58F * getMultiplier() * Config.getInstance().drowsiness_speed_multiplier * tickDelta);

            if (caffeine_effect) {
                if (caffeine_value < max_caffeine_value) {
                    caffeine_value += 1.0F / (60 * 20) * tickDelta;
                } else if (max_caffeine_value != 0) {
                    caffeine_effect = false;
                }
            } else {
                if (caffeine_value > max_caffeine_value * -0.75F) {
                    caffeine_value -= 1.0F / (dayLength / 4.0F) * tickDelta;
                }
            }

            if (sleeping_pills_effect) {
                if (sleeping_pills_value < max_sleeping_pills_value) {
                    sleeping_pills_value += 1.0F / (60 * 20) * Config.getInstance().sleeping_pills_speed_multiplier * tickDelta;
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

            if(getPlayer().isSleeping()) {
                if (getAmplifier() < Config.getInstance().min_drowsiness_for_sleep && !health.getDrunkenness().canSleepAnyway()) {
                    getPlayer().stopSleeping();
                    getPlayer().sendSystemMessage(Component.translatable(ZomboidHealthSystem.ID + ".message.dont_want_sleep"));
                }
            }
        }

        if(Config.getInstance().drowsiness_speed_multiplier == 0) {
            setValue(defaultValue());
        }
    }

    @Override
    public void sleep(int ticks) {
        setValue(value - (float) ticks / 8000.0F);
    }

    @Override
    public void onSleep(int sumTicks) {
        if(sumTicks >= 1000) {
            this.max_caffeine_value = 0;
            this.caffeine_value = 0;
            this.caffeine_effect = true;
            this.sleeping_pills_value = 0;
            this.max_sleeping_pills_value = 0;
            this.sleeping_pills_effect = true;
        }
    }

    @Override
    public boolean showIcon() {
        return this.getAmplifier() >= 1;
    }

    @Override
    public byte[] getNbt() {
        try {
            if (this.getValue() != 0 || this.caffeine_value != 0 || this.max_caffeine_value != 0) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                DataOutputStream out = new DataOutputStream(byteArrayOutputStream);
                out.writeFloat(value);
                out.writeFloat(caffeine_value);
                out.writeFloat(max_caffeine_value);
                out.writeBoolean(caffeine_effect);
                out.writeFloat(sleeping_pills_value);
                out.writeFloat(max_sleeping_pills_value);
                out.writeBoolean(sleeping_pills_effect);
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void readNbt(byte[] value) {
        try {
            if (value == null) {
                this.value = 0;
                this.caffeine_value = 0;
                this.max_caffeine_value = 0;
                this.sleeping_pills_value = 0;
                this.max_sleeping_pills_value = 0;
                this.sleeping_pills_effect = true;
                this.caffeine_effect = true;
            } else {
                DataInputStream in = new DataInputStream(new ByteArrayInputStream(value));
                this.value = in.readFloat();
                this.caffeine_value = in.readFloat();
                this.max_caffeine_value = in.readFloat();
                this.caffeine_effect = in.readBoolean();
                this.sleeping_pills_value = in.readFloat();
                this.max_sleeping_pills_value = in.readFloat();
                this.sleeping_pills_effect = in.readBoolean();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
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
        addValue(amount / dayLength);
    }
}