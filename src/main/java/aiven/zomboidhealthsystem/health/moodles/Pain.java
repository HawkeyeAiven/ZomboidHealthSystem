package aiven.zomboidhealthsystem.health.moodles;

import aiven.zomboidhealthsystem.Config;
import aiven.zomboidhealthsystem.health.Health;
import aiven.zomboidhealthsystem.health.bodyparts.BodyPart;

import de.marhali.json5.Json5;
import de.marhali.json5.Json5Object;

import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

import java.io.IOException;

public final class Pain extends Moodle {
    private static final Json5 mapper = new Json5();

    private float painkillerAmount = 0;
    private float painkillerMaxAmount = 0;
    private boolean painkillerEffect = true;

    public Pain(Health health) {
        super(health);
    }

    @Override
    public void update(float tickDelta) {
        Player player = this.getHealth().getPlayer();

        if(tickDelta != 0) {
            if (painkillerEffect) {
                if (painkillerAmount < painkillerMaxAmount) {
                    painkillerAmount += 1.0F / (60 * 20) * tickDelta;
                } else if (painkillerAmount != 0) {
                    painkillerEffect = false;
                }
            } else {
                if (painkillerAmount > 0) {
                    final float speed = 1.0F / (600 * 20) * tickDelta;
                    painkillerAmount -= speed;
                    painkillerMaxAmount -= speed;
                } else {
                    painkillerAmount = 0;
                    painkillerMaxAmount = 0;
                }
            }
        }

            if (player.isSleeping() && Config.getInstance().pain_keeps_awake && this.getPain() >= 1 && getHealth().getDrowsiness().getAmplifier() < 2) {
                player.stopSleeping();
                player.displayClientMessage(Component.translatable("zomboid-health-system.message.pain_not_sleep"), true);
            }


        if (this.getPain() >= 8) {
            health.addMobEffect(MobEffects.BLINDNESS, 0, 5 * 20);
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
            Json5Object object = new Json5Object();
            object.addProperty("painkiller_amount", painkillerAmount);
            object.addProperty("painkiller_max_amount", painkillerMaxAmount);
            object.addProperty("painkiller_effect", painkillerEffect);
            try {
                return mapper.serialize(object);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
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
            Json5Object object = mapper.parse(value).getAsJson5Object();
            this.painkillerAmount = object.get("painkiller_amount").getAsFloat();
            this.painkillerMaxAmount = object.get("painkiller_max_amount").getAsFloat();
            this.painkillerEffect = object.get("painkiller_effect").getAsBoolean();
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