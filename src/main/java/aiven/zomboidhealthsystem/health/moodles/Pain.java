package aiven.zomboidhealthsystem.health.moodles;

import aiven.zomboidhealthsystem.Config;
import aiven.zomboidhealthsystem.ZomboidHealthSystem;
import aiven.zomboidhealthsystem.health.Health;
import aiven.zomboidhealthsystem.health.bodyparts.BodyPart;

import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

import java.io.*;

public final class Pain extends Moodle {
    private float painkillerAmount = 0;
    private float painkillerMaxAmount = 0;
    private boolean painkillerEffect = true;

    public Pain(Health health) {
        super(health);
    }

    @Override
    public void update(float tickDelta) {
        Player player = this.health.getPlayer();

        if (tickDelta != 0) {
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
            player.displayClientMessage(Component.translatable(ZomboidHealthSystem.ID + ".message.pain_not_sleep"), true);
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
    public byte[] getNbt() {
        if(painkillerMaxAmount != 0 || painkillerAmount != 0) {
            try {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                DataOutputStream out = new DataOutputStream(byteArrayOutputStream);
                out.writeFloat(this.painkillerAmount);
                out.writeFloat(this.painkillerMaxAmount);
                out.writeBoolean(this.painkillerEffect);
                return byteArrayOutputStream.toByteArray();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            return null;
        }
    }

    @Override
    public void readNbt(byte[] value) {
        if(value == null) {
            this.painkillerAmount = 0;
            this.painkillerMaxAmount = 0;
            this.painkillerEffect = false;
        } else {
            try {
                DataInputStream in = new DataInputStream(new ByteArrayInputStream(value));
                this.painkillerAmount = in.readFloat();
                this.painkillerMaxAmount = in.readFloat();
                this.painkillerEffect = in.readBoolean();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void addPainkiller(float amount) {
        this.painkillerMaxAmount += amount;
        this.painkillerEffect = true;
    }

    public float getPain() {
        float p = 0;
        for (BodyPart part : this.getHealth().getBodyParts()) {
            float d = (part.getMaxHp() - part.getHp()) / 2.0F * (part.hasInfection() ? 2.0F : 1.0F);
            if (part.isBandaged()) {
                p += d / 1.25F;
            } else {
                p += d;
            }
        }
        float pain = p / (painkillerAmount + 1) * getMultiplier();
        return Math.max(0, pain);
    }

    public static class Nbt {
        public Float painkillerMaxAmount;
        public Float painkillerAmount;
        public Boolean painkillerEffect;
    }
}