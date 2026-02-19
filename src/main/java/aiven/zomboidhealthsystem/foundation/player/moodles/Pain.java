package aiven.zomboidhealthsystem.foundation.player.moodles;

import aiven.zomboidhealthsystem.Config;
import aiven.zomboidhealthsystem.foundation.player.Health;
import aiven.zomboidhealthsystem.foundation.player.bodyparts.BodyPart;
import aiven.zomboidhealthsystem.infrastructure.config.Json;
import aiven.zomboidhealthsystem.infrastructure.config.JsonBuilder;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

public class Pain extends Moodle {
    private float painkillerAmount = 0;
    private float painkillerMaxAmount = 0;
    private boolean painkillerEffect = true;


    public Pain(Health health) {
        super(health);
    }

    @Override
    public void update() {
        PlayerEntity player = this.getHealth().getPlayer();

        if(painkillerEffect) {
            if(painkillerAmount < painkillerMaxAmount) {
                painkillerAmount += 1.0F / (60 * 20) * Health.UPDATE_FREQUENCY;
            } else {
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

        if(player.isSleeping() && Config.PAIN_KEEPS_AWAKE.getValue() && this.getAmount() >= 1 && getHealth().getDrowsiness().getAmplifier() < 2){
            player.wakeUp();
            player.sendMessage(Text.translatable("zomboidhealthsystem.message.pain_not_sleep"),true);
        }
    }

    @Override
    public void onSleep() {
        super.onSleep();
        painkillerAmount = 0;
        painkillerMaxAmount = 0;
        painkillerEffect = true;
    }

    @Override
    public float getAmount() {
        return getPain();
    }

    @Override
    public void setAmount(float amount) {
    }

    @Override
    public String getId() {
        return "pain";
    }

    @Override
    public String getNbt() {
        if(this.painkillerAmount != 0) {
            JsonBuilder builder = new JsonBuilder();
            builder.append("painkiller_amount", String.valueOf(painkillerAmount));
            builder.append("painkiller_max_amount", String.valueOf(painkillerMaxAmount));
            builder.append("painkiller_effect", String.valueOf(painkillerEffect));
            return builder.toString();
        } else {
            return null;
        }
    }

    @Override
    public void readNbt(String value) {
        String amount = Json.getValue(value, "painkiller_amount");
        String max_amount = Json.getValue(value, "painkiller_max_amount");
        String effect = Json.getValue(value, "painkiller_effect");
        if(amount != null) {
            this.painkillerAmount = Float.parseFloat(amount);
        }
        if(max_amount != null) {
            this.painkillerMaxAmount = Float.parseFloat(max_amount);
        }
        if(effect != null) {
            this.painkillerEffect = Boolean.parseBoolean(effect);
        }
    }

    public void applyEffects() {
        if (this.getPain() >= 8) {
            health.addStatusEffect(StatusEffects.BLINDNESS, 0, 5 * 20);
        }
    }

    public void addPainkiller(float amount) {
        this.painkillerMaxAmount += amount;
        this.painkillerEffect = true;
    }

    public float getPain() {
        float pain = 0;
        for (BodyPart part : this.getHealth().getBodyParts()) {
            pain += part.getPain() / 2;
        }
        return pain / (painkillerAmount + 1);
    }
}