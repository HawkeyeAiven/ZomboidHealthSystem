package aiven.zomboidhealthsystem.foundation.player.moodles;

import aiven.zomboidhealthsystem.Config;
import aiven.zomboidhealthsystem.ModDamageTypes;
import aiven.zomboidhealthsystem.ModStatusEffects;
import aiven.zomboidhealthsystem.foundation.player.Health;
import aiven.zomboidhealthsystem.foundation.utility.Util;
import aiven.zomboidhealthsystem.foundation.world.ModServer;
import aiven.zomboidhealthsystem.infrastructure.config.Json;
import aiven.zomboidhealthsystem.infrastructure.config.JsonBuilder;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.text.Text;

public class Drowsiness extends Moodle {
    private final int DROWSINESS_TIME;

    public DamageSource source = Util.getDamageSource(ModDamageTypes.DROWSINESS, getPlayer().getWorld());
    private float caffeine = 0;
    private float max_caffeine = 0;
    private boolean caffeine_effect = true;

    private float sleeping_pills = 0;
    private float max_sleeping_pills = 0;

    public Drowsiness(Health health) {
        super(health);
        DROWSINESS_TIME = 24000 * ModServer.WORLD_SETTINGS.getDayLengthMultiplier();
    }

    @Override
    StatusEffect getEffect() {
        return ModStatusEffects.DROWSINESS;
    }

    @Override
    public String getId() {
        return "drowsiness";
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

            return builder.toString();
        } else {
            return null;
        }
    }

    @Override
    public void readNbt(String value) {
        String amount = Json.getValue(value, "amount");
        String caffeine = Json.getValue(value,"caffeine");
        String max_caffeine = Json.getValue(value,"max_caffeine");
        String caffeine_effect = Json.getValue(value, "caffeine_effect");
        String sleeping_pills = Json.getValue(value, "sleeping_pills");
        String max_sleeping_pills = Json.getValue(value, "max_sleeping_pills");

        if(amount != null){
            this.setAmount(Float.parseFloat(amount));
        }
        if(caffeine != null && max_caffeine != null){
            this.caffeine = Float.parseFloat(caffeine);
            this.max_caffeine = Float.parseFloat(max_caffeine);
        }
        if(caffeine_effect != null) {
            this.caffeine_effect = Boolean.parseBoolean(caffeine_effect);
        }
        if(sleeping_pills != null && max_sleeping_pills != null) {
            this.sleeping_pills = Float.parseFloat(sleeping_pills);
            this.max_sleeping_pills = Float.parseFloat(max_sleeping_pills);
        }
    }

    @Override
    public void update() {
        super.update();
        addTicks(1 * (this.getHealth().getExhaustion().getAmount() + 1) * 1.85F * Config.DROWSINESS_MULTIPLIER.getValue() * Health.UPDATE_FREQUENCY);

        if(caffeine_effect) {
            if(caffeine < max_caffeine) {
                caffeine += 1.0F / (60 * 20) * Health.UPDATE_FREQUENCY;
            } else if(max_caffeine != 0){
                caffeine_effect = false;
            }
        } else {
            if(caffeine > max_caffeine * -0.75F) {
                caffeine -= 1.0F / (DROWSINESS_TIME / 4.0F) * Health.UPDATE_FREQUENCY;
            }
        }

        if(sleeping_pills < max_sleeping_pills) {
            final float speed = Config.SLEEPING_PILLS_SPEED.getValue() * Health.UPDATE_FREQUENCY;
            sleeping_pills = Math.min(sleeping_pills + speed, max_sleeping_pills);
            addAmount(speed);
        } else {
            sleeping_pills = 0;
            max_sleeping_pills = 0;
        }

        getHealth().getExhaustion().addMultiplier((getAmplifier() / 3) + 1);

        if (this.getAmplifier() >= 1) {
            if (random((int) (5 * 60 * 20 / this.getAmplifier()))) {
                this.getHealth().addStatusEffect(StatusEffects.BLINDNESS, 1, 5 * 20);
            }

            if (this.getAmplifier() >= 2) {
                if (random((int) (5 * 60 * 20 / (this.getAmplifier() / 2))) && this.getHealth().getPlayer().getMovementSpeed() > 0.1f) {
                    this.getHealth().stumble();
                }

                getHealth().getExhaustion().addMultiplier(getAmplifier());

                if (this.getAmplifier() >= 3) {
                    this.getHealth().addStatusEffect(StatusEffects.MINING_FATIGUE, (int) (this.getAmplifier() / 2), 15 * 20);

                    if(this.getAmplifier() >= 5){
                        this.getHealth().onDeath(this.source);
                    }
                }
            }
        }

        if(getPlayer().isSleeping() && getAmplifier() < Config.MIN_DROWSINESS_FOR_SLEEP.getValue()) {
            getPlayer().wakeUp();
            getPlayer().sendMessage(Text.translatable("zomboidhealthsystem.message.dont_want_sleep"), true);
        }
    }

    @Override
    public void onSleep() {
        this.max_caffeine = 0;
        this.caffeine = 0;
        this.caffeine_effect = true;
        this.sleeping_pills = 0;
        this.max_sleeping_pills = 0;
        this.amount = 0;
    }

    @Override
    public boolean hasIcon() {
        return this.getAmplifier() >= 1;
    }

    @Override
    public int getEffectAmplifier() {
        return (int) getAmplifier();
    }

    public float getAmplifier() {
        return amount - caffeine;
    }

    public void addCaffeine(float amount) {
        this.max_caffeine += amount;
        this.caffeine_effect = true;
    }

    public void addSleepingPills(float amount) {
        this.max_sleeping_pills += amount;
    }

    public boolean hasCaffeine() {
        return caffeine != 0 && max_caffeine != 0;
    }

    public void addTicks(float amount) {
        addAmount(amount / DROWSINESS_TIME);
    }
}