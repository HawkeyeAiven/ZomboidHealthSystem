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
        super.update();

        this.setAmount(getPain());

        PlayerEntity player = this.getHealth().getPlayer();

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

        if(player.isSleeping() && Config.PAIN_KEEPS_AWAKE.getValue() && this.getAmount() >= 1 && getHealth().getDrowsiness().getAmplifier() < 2){
            player.wakeUp();
            player.sendMessage(Text.translatable("zomboidhealthsystem.message.pain_not_sleep"),true);
        }
    }

    @Override
    public void onSleep(int sumTicks) {
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
        if(getAmount() != 0 || painkillerMaxAmount != 0 || painkillerAmount != 0) {
            JsonBuilder builder = new JsonBuilder();
            if(getAmount() != 0) {
                builder.append("amount", String.valueOf(getAmount()));
            }
            if(painkillerAmount != 0) {
                builder.append("painkiller_amount", String.valueOf(painkillerAmount));
            }
            if(painkillerMaxAmount != 0) {
                builder.append("painkiller_max_amount", String.valueOf(painkillerMaxAmount));
            }
            if(!painkillerEffect) {
                builder.append("painkiller_effect", String.valueOf(painkillerEffect));
            }
            return builder.toString();
        } else {
            return null;
        }
    }

    @Override
    public void readNbt(String value) {
        if(value == null) {
            this.amount = 0;
            this.painkillerAmount = 0;
            this.painkillerMaxAmount = 0;
            this.painkillerEffect = false;
            return;
        }
        String amount = Json.getValue(value, "amount");
        String painkiller_amount = Json.getValue(value, "painkiller_amount");
        String painkiller_max_amount = Json.getValue(value, "painkiller_max_amount");
        String painkiller_effect = Json.getValue(value, "painkiller_effect");
        if(amount != null) {
            this.amount = Float.parseFloat(amount);
        } else {
            this.amount = 0;
        }
        if(painkiller_amount != null) {
            this.painkillerAmount = Float.parseFloat(painkiller_amount);
        } else {
            this.painkillerAmount = 0;
        }
        if(painkiller_max_amount != null) {
            this.painkillerMaxAmount = Float.parseFloat(painkiller_max_amount);
        } else {
            this.painkillerMaxAmount = 0;
        }
        if(painkiller_effect != null) {
            this.painkillerEffect = Boolean.parseBoolean(painkiller_effect);
        } else {
            this.painkillerEffect = true;
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