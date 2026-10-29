package aiven.zomboidhealthsystem.health.moodles;

import aiven.zomboidhealthsystem.Config;
import aiven.zomboidhealthsystem.health.Health;
import aiven.zomboidhealthsystem.health.bodyparts.BodyPart;

import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.JsonNodeFactory;
import tools.jackson.databind.node.ObjectNode;

public final class Pain extends Moodle {
    private static final ObjectMapper mapper = new ObjectMapper();

    private float painkillerAmount = 0;
    private float painkillerMaxAmount = 0;
    private boolean painkillerEffect = true;

    public Pain(Health health) {
        super(health);
    }

    @Override
    public void update() {
        Player player = this.getHealth().getPlayer();

        if(painkillerEffect) {
            if(painkillerAmount < painkillerMaxAmount) {
                painkillerAmount += 1.0F / (60 * 20) * Health.UPDATE_FREQUENCY;
            } else if(painkillerAmount != 0){
                painkillerEffect = false;
            }
        } else {
            if(painkillerAmount > 0) {
                final float speed = 1.0F / (600 * 20) * Health.UPDATE_FREQUENCY;
                painkillerAmount -= speed;
                painkillerMaxAmount -= speed;
            } else {
                painkillerAmount = 0;
                painkillerMaxAmount = 0;
            }
        }

        if(player.isSleeping() && Config.getInstance().pain_keeps_awake && this.getPain() >= 1 && getHealth().getDrowsiness().getAmplifier() < 2){
            player.stopSleeping();
            player.displayClientMessage(Component.translatable("zomboid-health-system.message.pain_not_sleep"),true);
        }
    }

    @Override
    public int getAmplifier() {
        return (int) getPain();
    }

    @Override
    public void sleep(int sumTicks) {
        if(sumTicks >= 3000) {
            painkillerEffect = true;
            painkillerAmount = 0;
            painkillerMaxAmount = 0;
        }
    }

    @Override
    public String getId() {
        return "pain";
    }

    @Override
    public String getNbt() {
        if(painkillerMaxAmount != 0 || painkillerAmount != 0) {
            ObjectNode objectNode = new ObjectNode(JsonNodeFactory.instance);
            objectNode.put("painkiller_amount", painkillerAmount);
            objectNode.put("painkiller_max_amount", painkillerMaxAmount);
            objectNode.put("painkiller_effect", painkillerEffect);
            return mapper.writeValueAsString(objectNode);
        } else {
            return null;
        }
    }

    @Override
    public void readNbt(String value) {
        if(value == null) {
            this.painkillerAmount = 0;
            this.painkillerMaxAmount = 0;
            this.painkillerEffect = false;
        } else {
            ObjectNode objectNode = mapper.readTree(value).asObject();
            this.painkillerAmount = objectNode.get("painkiller_amount").asFloat();
            this.painkillerMaxAmount = objectNode.get("painkiller_max_amount").asFloat();
            this.painkillerEffect = objectNode.get("painkiller_effect").asBoolean();
        }
    }

    public void applyEffects() {
        if (this.getPain() >= 8) {
            health.addMobEffect(MobEffects.BLINDNESS, 0, 5 * 20);
        }
    }

    public void addPainkiller(float amount) {
        this.painkillerMaxAmount += amount;
        this.painkillerEffect = true;
    }

    public float getPain() {
        float pain = 0;
        for (BodyPart part : this.getHealth().getBodyParts()) {
            float d = (part.getMaxHp() - part.getHp()) / 2.0F * (part.hasInfection() ? 2.0F : 1.0F);
            if (part.isBandaged())
                pain += d / 1.25F;
            else {
                pain += d;
            }
        }
        return pain / (painkillerAmount + 1) * getMultiplier();
    }
}