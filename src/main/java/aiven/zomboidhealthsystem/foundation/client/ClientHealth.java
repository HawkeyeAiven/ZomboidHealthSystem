package aiven.zomboidhealthsystem.foundation.client;

import aiven.zomboidhealthsystem.foundation.items.BandageItem;
import aiven.zomboidhealthsystem.foundation.player.Health;
import aiven.zomboidhealthsystem.infrastructure.config.Json;
import net.minecraft.item.Item;
import net.minecraft.network.PacketByteBuf;

public class ClientHealth {
    private final BodyPart head, body, leftArm, rightArm, leftLeg, rightLeg, leftFoot, rightFoot;
    private final BodyPart[] bodyParts;
    private float hp = 100;

    public ClientHealth() {
        this.head = new BodyPart(4);
        this.body = new BodyPart(6);
        this.leftArm = new BodyPart(4);
        this.rightArm = new BodyPart(4);
        this.leftLeg = new BodyPart(4);
        this.rightLeg = new BodyPart(4);
        this.leftFoot = new BodyPart(4);
        this.rightFoot = new BodyPart(4);

        this.bodyParts = new BodyPart[]{head,body,leftArm,rightArm,leftLeg,rightLeg,leftFoot,rightFoot};
    }

    public void onPacket(PacketByteBuf packetByteBuf) {
        String health = packetByteBuf.readString();
        set(health);
    }

    private void set(String health) {
        String[] ids = new String[]{Health.HEAD_ID,Health.BODY_ID,Health.LEFT_ARM_ID,Health.RIGHT_ARM_ID,Health.LEFT_LEG_ID,Health.RIGHT_LEG_ID,Health.LEFT_FOOT_ID,Health.RIGHT_FOOT_ID};

        {
            String playerHp = Json.getValue(health, "player_hp");
            if(playerHp != null) {
                this.setHp(Float.parseFloat(playerHp));
            } else {
                this.setHp(100);
            }
        }

        for(int index = 0; index < ids.length; index++) {
            String id = ids[index];
            String value = Json.getValue(health, id);
            BodyPart bodyPart = this.indexOf(index);

            if(value == null) {
                bodyPart.reset();
            } else {

                {
                    String hpString = Json.getValue(value, "hp");
                    if(hpString != null) {
                        bodyPart.setHp(Float.parseFloat(hpString));
                    } else {
                        bodyPart.setHp(bodyPart.getMaxHp());
                    }
                }

                {
                    String addHpString = Json.getValue(value, "add_hp");
                    if(addHpString != null) {
                        bodyPart.setAdditionalHp(Float.parseFloat(addHpString));
                    } else {
                        bodyPart.setAdditionalHp(0);
                    }
                }

                boolean stopBleeding = false;
                {
                    String bandageItemRawIdString = Json.getValue(value, "bandage_item");
                    if(bandageItemRawIdString != null) {
                        int rawId = Integer.parseInt(bandageItemRawIdString);
                        Item item = Item.byRawId(rawId);
                        try {
                            BandageItem bandage = (BandageItem) item;
                            stopBleeding = bandage.isStopBleeding();
                            bodyPart.setBandaged(true);
                            bodyPart.setDirtyBandage(bandage.isDirty());
                        } catch (Exception ignored) {
                        }
                    } else {
                        bodyPart.setBandaged(false);
                        bodyPart.setDirtyBandage(false);
                    }
                }

                if(!stopBleeding) {
                    String bleedingString = Json.getValue(value, "bleeding");
                    bodyPart.setBleeding(bleedingString != null);
                } else {
                    bodyPart.setBleeding(false);
                }

                {
                    String infection = Json.getValue(value, "infection");
                    if(infection != null) {
                        int ticks = Integer.parseInt(infection);
                        bodyPart.setInfection(ticks > Health.INFECTION_TIME);
                    } else {
                        bodyPart.setInfection(false);
                    }
                }

            }
        }
    }

    public BodyPart indexOf(int index) {
        return bodyParts[index];
    }

    public void setHp(float hp) {
        this.hp = hp;
    }

    public float getHp() {
        return hp;
    }

    public float getSumHp() {
        float sum = 0;
        for(BodyPart bodyPart : bodyParts) {
            sum += bodyPart.getHp();
        }
        return sum;
    }

    public float getMaxSumHp() {
        return 34.0F;
    }

    public float getSumHpPercent() {
        return getSumHp() / getMaxSumHp();
    }

    public BodyPart getBody() {
        return body;
    }

    public BodyPart getHead() {
        return head;
    }

    public BodyPart getLeftArm() {
        return leftArm;
    }

    public BodyPart getLeftFoot() {
        return leftFoot;
    }

    public BodyPart getLeftLeg() {
        return leftLeg;
    }

    public BodyPart getRightArm() {
        return rightArm;
    }

    public BodyPart getRightFoot() {
        return rightFoot;
    }

    public BodyPart getRightLeg() {
        return rightLeg;
    }

    public static class BodyPart {
        private float hp;
        private float additionalHp = 0;
        private final float maxHp;
        boolean isBleeding = false;
        boolean isInfection = false;
        boolean isBandaged = false;
        boolean isDirtyBandage = false;

        public BodyPart(float maxHp) {
            this.maxHp = maxHp;
            this.hp = maxHp;
        }

        public void setInfection(boolean infection) {
            isInfection = infection;
        }

        public void setBleeding(boolean bleeding) {
            isBleeding = bleeding;
        }

        public void setBandaged(boolean bandaged) {
            isBandaged = bandaged;
        }

        public void setDirtyBandage(boolean dirtyBandage) {
            isDirtyBandage = dirtyBandage;
        }

        public boolean isBleeding() {
            return isBleeding;
        }

        public boolean isInfection() {
            return isInfection;
        }

        public boolean isBandaged() {
            return isBandaged;
        }

        public boolean isDirtyBandage() {
            return isDirtyBandage;
        }

        public float getMaxHp() {
            return maxHp;
        }

        public float getHp() {
            return hp;
        }

        public void setHp(float hp) {
            this.hp = hp;
        }

        public float getHpPercent() {
            return getHp() / getMaxHp();
        }

        public float getAdditionalHp() {
            return additionalHp;
        }

        public void setAdditionalHp(float additionalHp) {
            this.additionalHp = additionalHp;
        }

        public void reset() {
            setHp(maxHp);
            setBleeding(false);
            setInfection(false);
            setBandaged(false);
            setDirtyBandage(false);
            setAdditionalHp(0);
        }
    }
}
