package aiven.zomboidhealthsystem.health.moodles;

import aiven.zomboidhealthsystem.Config;
import aiven.zomboidhealthsystem.ModDamageTypes;
import aiven.zomboidhealthsystem.ZomboidHealthSystem;
import aiven.zomboidhealthsystem.health.Health;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffects;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.JsonNodeFactory;
import tools.jackson.databind.node.ObjectNode;

public final class Drowsiness extends ImportantMoodle {
    private static final ObjectMapper mapper = new ObjectMapper();

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
    protected void _update() {
        addTicks(1.0F * 1.58F * getMultiplier() * Config.getInstance().drowsiness_multiplier * Health.UPDATE_FREQUENCY);

        if(caffeine_effect) {
            if(caffeine_value < max_caffeine_value) {
                caffeine_value += 1.0F / (60 * 20) * Health.UPDATE_FREQUENCY;
            } else if(max_caffeine_value != 0){
                caffeine_effect = false;
            }
        } else {
            if(caffeine_value > max_caffeine_value * -0.75F) {
                caffeine_value -= 1.0F / (drowsinessTime / 4.0F) * Health.UPDATE_FREQUENCY;
            }
        }

        if(sleeping_pills_effect) {
            if(sleeping_pills_value < max_sleeping_pills_value) {
                sleeping_pills_value += 1.0F / (60 * 20) * Config.getInstance().sleeping_pills_multiplier * Health.UPDATE_FREQUENCY;
            } else if(max_sleeping_pills_value != 0) {
                sleeping_pills_effect = false;
            }
        } else {
            if(sleeping_pills_value > 0) {
                sleeping_pills_value -= 1.0F / (5 * 60 * 20) * Health.UPDATE_FREQUENCY;
            } else {
                sleeping_pills_value = 0;
                max_sleeping_pills_value = 0;
                sleeping_pills_effect = true;
            }
        }

        this.getHealth().getExhaustion().setMultiplier(this, ((float) getAmplifier() / 2) + 1);

        if (this.getAmplifier() >= 1) {
            if (once(5 * 60 * 20 / this.getAmplifier())) {
                this.getHealth().addMobEffect(MobEffects.BLINDNESS, 0, 5 * 20);
            }

            if (this.getAmplifier() >= 2) {
                if (this.getAmplifier() >= 3) {
                    this.getHealth().addMobEffect(MobEffects.DIG_SLOWDOWN, (this.getAmplifier() / 2) - 1, 15 * 20);
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
            ObjectNode objectNode = new ObjectNode(JsonNodeFactory.instance);

            objectNode.put("amount", this.getValue());
            objectNode.put("caffeine_value", this.caffeine_value);
            objectNode.put("max_caffeine_value", this.max_caffeine_value);
            objectNode.put("caffeine_effect", this.caffeine_effect);
            objectNode.put("sleeping_pills_value", this.sleeping_pills_value);
            objectNode.put("max_sleeping_pills_value", this.max_sleeping_pills_value);
            objectNode.put("sleeping_pills_effect", this.sleeping_pills_effect);

            return mapper.writeValueAsString(objectNode);
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
            ObjectNode objectNode = mapper.readTree(value).asObject();

            this.value = objectNode.get("amount").asFloat();
            this.caffeine_value = objectNode.get("caffeine_value").asFloat();
            this.max_caffeine_value = objectNode.get("max_caffeine_value").asFloat();
            this.caffeine_effect = objectNode.get("caffeine_effect").asBoolean();
            this.sleeping_pills_value = objectNode.get("sleeping_pills_value").asFloat();
            this.max_sleeping_pills_value = objectNode.get("max_sleeping_pills_value").asFloat();
            this.sleeping_pills_effect = objectNode.get("sleeping_pills_effect").asBoolean();
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

    public boolean hasCaffeine() {
        return caffeine_value != 0 && max_caffeine_value != 0;
    }

    public void addTicks(float amount) {
        addValue(amount / drowsinessTime);
    }
}