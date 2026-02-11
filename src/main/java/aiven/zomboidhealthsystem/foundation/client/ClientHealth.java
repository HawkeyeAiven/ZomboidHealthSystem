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
        this.head = new BodyPart(4, Health.HEAD_ID);
        this.body = new BodyPart(6, Health.BODY_ID);
        this.leftArm = new BodyPart(4, Health.LEFT_ARM_ID);
        this.rightArm = new BodyPart(4, Health.RIGHT_ARM_ID);
        this.leftLeg = new BodyPart(4, Health.LEFT_LEG_ID);
        this.rightLeg = new BodyPart(4, Health.RIGHT_LEG_ID);
        this.leftFoot = new BodyPart(4, Health.LEFT_FOOT_ID);
        this.rightFoot = new BodyPart(4, Health.RIGHT_FOOT_ID);

        this.bodyParts = new BodyPart[]{head,body,leftArm,rightArm,leftLeg,rightLeg,leftFoot,rightFoot};
    }

    public void onPacket(PacketByteBuf packetByteBuf) {
        String health = packetByteBuf.readString();
        set(health);
    }

    private void set(String health) {
        {
            String playerHp = Json.getValue(health, "player_hp");
            if(playerHp != null) {
                this.setHp(Float.parseFloat(playerHp));
            } else {
                this.setHp(100);
            }
        }

        for(int index = 0; index < bodyParts.length; index++) {
            String id = bodyParts[index].getId();
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

                {
                    String bandageItemRawIdString = Json.getValue(value, "bandage_item");
                    if(bandageItemRawIdString != null) {
                        int rawId = Integer.parseInt(bandageItemRawIdString);
                        Item item = Item.byRawId(rawId);
                        try {
                            BandageItem bandage = (BandageItem) item;
                            bodyPart.setBandageStopBleeding(bandage.isStopBleeding());
                            bodyPart.setBandaged(true);
                            bodyPart.setDirtyBandage(bandage.isDirty());
                        } catch (Exception ignored) {
                        }
                    } else {
                        bodyPart.setBandaged(false);
                        bodyPart.setDirtyBandage(false);
                    }
                }

                String bleedingString = Json.getValue(value, "bleeding");
                bodyPart.setBleeding(bleedingString != null);

                {
                    String infection = Json.getValue(value, "infection");
                    if(infection != null) {
                        bodyPart.setInfection(Boolean.parseBoolean(infection));
                    } else {
                        bodyPart.setInfection(false);
                    }
                }

            }
        }
    }

    public BodyPart get(String id) {
        for(BodyPart part : bodyParts) {
            if(part.getId().equals(id)) {
                return part;
            }
        }
        return null;
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

    public BodyPart[] getBodyParts() {
        return bodyParts;
    }

    public static class BodyPart {
        private float hp;
        private float additionalHp = 0;
        private final float maxHp;
        boolean isBleeding = false;
        boolean isInfection = false;
        boolean isBandaged = false;
        boolean isDirtyBandage = false;
        boolean isBandageStopBleeding = false;
        private final String id;

        public BodyPart(float maxHp, String id) {
            this.maxHp = maxHp;
            this.hp = maxHp;
            this.id = id;
        }

        public String getId() {
            return id;
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

        public void setBandageStopBleeding(boolean bandageStopBleeding) {
            isBandageStopBleeding = bandageStopBleeding;
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

        public boolean isBandageStopBleeding() {
            return isBandageStopBleeding;
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
            setBandageStopBleeding(false);
        }
    }
}
