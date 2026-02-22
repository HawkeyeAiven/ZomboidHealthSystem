package aiven.zomboidhealthsystem.foundation.player.moodles;

import aiven.zomboidhealthsystem.Config;
import aiven.zomboidhealthsystem.ModDamageTypes;
import aiven.zomboidhealthsystem.foundation.player.Health;
import aiven.zomboidhealthsystem.foundation.utility.Util;
import aiven.zomboidhealthsystem.foundation.world.ModServer;
import aiven.zomboidhealthsystem.infrastructure.config.Json;
import aiven.zomboidhealthsystem.infrastructure.config.JsonBuilder;
import net.minecraft.entity.effect.StatusEffects;

public class Drowsiness extends Moodle {
    private int drowsinessTime;

    private float caffeine = 0;
    private float max_caffeine = 0;
    private boolean caffeine_effect = true;

    private float sleeping_pills = 0;
    private float max_sleeping_pills = 0;
    private boolean sleeping_pills_effect = true;

    public Drowsiness(Health health) {
        super(health);
        if(getPlayer() != null && !getPlayer().getWorld().isClient()) {
            drowsinessTime = 24000 * ModServer.WORLD_SETTINGS.getDayLengthMultiplier();
        }
    }

    @Override
    public String getId() {
        return "drowsiness";
    }

    @Override
    public void update() {
        super.update();
        addTicks(1.0F * 1.90F * getMultiplier() * Config.DROWSINESS_MULTIPLIER.getValue() * Health.UPDATE_FREQUENCY);

        if(caffeine_effect) {
            if(caffeine < max_caffeine) {
                caffeine += 1.0F / (60 * 20) * Health.UPDATE_FREQUENCY;
            } else if(max_caffeine != 0){
                caffeine_effect = false;
            }
        } else {
            if(caffeine > max_caffeine * -0.75F) {
                caffeine -= 1.0F / (drowsinessTime / 4.0F) * Health.UPDATE_FREQUENCY;
            }
        }

        if(sleeping_pills_effect) {
            if(sleeping_pills < max_sleeping_pills) {
                sleeping_pills += 1.0F / (60 * 20) * Config.SLEEPING_PILLS_MULTIPLIER.getValue() * Health.UPDATE_FREQUENCY;
            } else if(max_sleeping_pills != 0) {
                sleeping_pills_effect = false;
            }
        } else {
            if(sleeping_pills > 0) {
                sleeping_pills -= 1.0F / (5 * 60 * 20) * Health.UPDATE_FREQUENCY;
            } else {
                sleeping_pills = 0;
                max_sleeping_pills = 0;
                sleeping_pills_effect = true;
            }
        }

        this.getHealth().getExhaustion().addMultiplier(this, ((float) getAmplifier() / 2) + 1);

        if (this.getAmplifier() >= 1) {
            if (once(5 * 60 * 20 / this.getAmplifier())) {
                this.getHealth().addStatusEffect(StatusEffects.BLINDNESS, 0, 5 * 20);
            }

            if (this.getAmplifier() >= 2) {
                if (once(5 * 60 * 20 / (this.getAmplifier() / 2)) && this.getHealth().getPlayer().getMovementSpeed() > 0.1f) {
                    this.getHealth().stumble();
                }

                if (this.getAmplifier() >= 3) {
                    this.getHealth().addStatusEffect(StatusEffects.MINING_FATIGUE, (int) (this.getAmplifier() / 2) - 1, 15 * 20);

                    if(this.getAmplifier() >= 5){
                        this.getHealth().onDeath(Util.getDamageSource(ModDamageTypes.DROWSINESS, getPlayer().getWorld()));
                    }
                }
            }
        }
    }


    @Override
    public void sleep(int ticks) {
        super.sleep(ticks);
        this.amount = Math.max(0, amount - (float) ticks / 8000.0F);
    }

    @Override
    public void onSleep(int sumTicks) {
        super.onSleep(sumTicks);
        if(sumTicks >= 1000) {
            this.max_caffeine = 0;
            this.caffeine = 0;
            this.caffeine_effect = true;
            this.sleeping_pills = 0;
            this.max_sleeping_pills = 0;
            this.sleeping_pills_effect = true;
        }
    }

    @Override
    public boolean showIcon() {
        return this.getAmplifier() >= 1;
    }

    @Override
    public String getNbt() {
        if(this.getAmount() != 0 || this.caffeine != 0 || this.max_caffeine != 0) {
            JsonBuilder builder = new JsonBuilder();

            if(this.getAmount() != 0) {
                builder.append("amount", String.valueOf(this.getAmount()));
            }
            if(this.caffeine != 0) {
                builder.append("caffeine", String.valueOf(this.caffeine));
            }
            if(this.max_caffeine != 0) {
                builder.append("max_caffeine", String.valueOf(this.max_caffeine));
            }
            if(!this.caffeine_effect) {
                builder.append("caffeine_effect", String.valueOf(this.caffeine_effect));
            }
            if(this.sleeping_pills != 0) {
                builder.append("sleeping_pills", String.valueOf(this.sleeping_pills));
            }
            if(this.max_sleeping_pills != 0) {
                builder.append("max_sleeping_pills", String.valueOf(this.max_sleeping_pills));
            }
            if(!this.sleeping_pills_effect) {
                builder.append("sleeping_pills_effect", String.valueOf(this.sleeping_pills_effect));
            }

            return builder.toString();
        } else {
            return null;
        }
    }

    @Override
    public void readNbt(String value) {
        if(value == null) {
            this.caffeine = 0;
            this.max_caffeine = 0;
            this.sleeping_pills = 0;
            this.max_sleeping_pills = 0;
            this.amount = 0;
            this.sleeping_pills_effect = true;
            this.caffeine_effect = true;
            return;
        }
        String amount = Json.getValue(value, "amount");
        String caffeine = Json.getValue(value,"caffeine");
        String max_caffeine = Json.getValue(value,"max_caffeine");
        String caffeine_effect = Json.getValue(value, "caffeine_effect");
        String sleeping_pills = Json.getValue(value, "sleeping_pills");
        String max_sleeping_pills = Json.getValue(value, "max_sleeping_pills");
        String sleeping_pills_effect = Json.getValue(value, "sleeping_pills_effect");

        if(amount != null){
            this.setAmount(Float.parseFloat(amount));
        } else {
            this.amount = 0;
        }
        if(caffeine != null && max_caffeine != null){
            this.caffeine = Float.parseFloat(caffeine);
            this.max_caffeine = Float.parseFloat(max_caffeine);
        } else {
            this.caffeine = 0;
            this.max_caffeine = 0;
        }
        if(caffeine_effect != null) {
            this.caffeine_effect = Boolean.parseBoolean(caffeine_effect);
        } else {
            this.caffeine_effect = false;
        }
        if(sleeping_pills != null && max_sleeping_pills != null) {
            this.sleeping_pills = Float.parseFloat(sleeping_pills);
            this.max_sleeping_pills = Float.parseFloat(max_sleeping_pills);
        } else {
            this.sleeping_pills = 0;
            this.max_sleeping_pills = 0;
        }
        if(sleeping_pills_effect != null) {
            this.sleeping_pills_effect = Boolean.parseBoolean(sleeping_pills_effect);
        } else {
            this.sleeping_pills_effect = true;
        }
    }

    public int getAmplifier() {
        return (int) (amount - caffeine + sleeping_pills);
    }

    public void addCaffeine(float amount) {
        this.max_caffeine += amount;
        this.caffeine_effect = true;
    }

    public void addSleepingPills(float amount) {
        this.max_sleeping_pills += amount;
        this.sleeping_pills_effect = true;
    }

    public boolean hasCaffeine() {
        return caffeine != 0 && max_caffeine != 0;
    }

    @Override
    public void setAmount(float amount) {
        this.amount = Math.max(amount, 0);
    }

    public void addTicks(float amount) {
        addAmount(amount / drowsinessTime);
    }
}