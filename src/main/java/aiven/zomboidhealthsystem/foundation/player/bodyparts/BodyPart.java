package aiven.zomboidhealthsystem.foundation.player.bodyparts;

import aiven.zomboidhealthsystem.Config;
import aiven.zomboidhealthsystem.ModDamageTypes;
import aiven.zomboidhealthsystem.foundation.items.BandageItem;
import aiven.zomboidhealthsystem.foundation.player.Health;
import aiven.zomboidhealthsystem.foundation.utility.EffectAmplifiers;
import aiven.zomboidhealthsystem.foundation.utility.Util;
import aiven.zomboidhealthsystem.infrastructure.config.Json;
import aiven.zomboidhealthsystem.infrastructure.config.JsonBuilder;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public abstract class BodyPart {
    private final Health health;
    private float hp;
    private float additionalHp;
    private final PlayerEntity player;
    private final String id;
    private int bandageTime = 0;
    private BandageItem bandageItem;
    private BandageItem lastBandageItem;
    private float bleeding = 0;
    private boolean infection = false;

    public BodyPart(Health health, float hp, PlayerEntity player, String id) {
        this.health = health;
        this.hp = hp;
        this.player = player;
        this.id = id;
    }

    public float heal(float amount) {
        if (!this.isFullHp()) {
            if (this.getHp() + amount > this.getMaxHp()) {
                float d = getHp() + amount - this.getMaxHp();
                this.setHp(this.getMaxHp());
                return d;
            } else {
                this.setHp(this.getHp() + amount);
                return 0;
            }
        } else {
            return amount;
        }
    }

    public float damage(float amount, DamageSource source) {
        if(getAdditionalHp() > 0) {
            float d = amount - additionalHp;
            setAdditionalHp(Math.max(0, additionalHp - amount));
            amount = d;
        }

        if(amount <= 0) {
            return 0;
        }

        float d = amount - this.hp;
        this.hp = Math.max(this.hp - amount, 0);
        if (Util.random(getBleedingChance(amount, source))) {
            this.setBleeding((this.getMaxHp() - this.getHp()) / 1.85F);
        }

        return Math.max(0, d);
    }

    public void update() {
        if(getHp() >= getMaxHp()) {
            setBleeding(0);
        }

        if (this.isBandaged()) {
            this.heal(
                    bandageItem.getHealAmount()
                            / Health.BANDAGE_HEAL_TIME
                            / (this.hasInfection() ? 3 : 1)
                            * (this.getHealth().getHunger().getAmount() < 0 ? 1.25F : 1.0F)
                            / (float) Math.sqrt(Math.max(getHealth().getHunger().getAmount(), 1))
                            * Health.UPDATE_FREQUENCY
            );

            this.setBandageTime(this.getBandageTime() + Health.UPDATE_FREQUENCY);

            if (bandageItem.isDirty() && isBleeding()) {
                if(Util.random(Config.INFECTION_CHANCE.getValue() * Health.UPDATE_FREQUENCY)) {
                    this.infection = true;
                }
            }

            if (bandageItem.isStopBleeding()) {
                if (this.isBleeding()) {
                    this.setBleeding(this.getBleeding() - (0.00015F * Health.UPDATE_FREQUENCY));

                    if (this.getBandageTime() > Health.BANDAGE_BECOMES_DIRTY_AFTER / (this.getBleeding() + 0.5F)) {

                        this.setBandageItem(this.bandageItem.getDirtyBandageItem());
                    }
                }
            }
        }
        if(!this.isBandaged() || !this.bandageItem.isStopBleeding()) {
            this.getHealth().damagePlayerHp(
                    Util.getDamageSource(ModDamageTypes.BLEEDING, getPlayer().getWorld()),
                    (this.getBleeding() / 30) * Health.UPDATE_FREQUENCY);
        }
    }

    public float getBleedingChance(float damage, DamageSource source) {
        if(source == null) {
            return 0;
        }

        float chance = Config.AVERAGE_BLEEDING_CHANCE.getValue() * damage;
        chance *= Math.max(1, (this.getMaxHp() - this.getHp()) * 0.75F);

        if((source.isOf(DamageTypes.FALL) || source.isOf(DamageTypes.HOT_FLOOR)) && !getHealth().getPlayer().isCrawling()) {
            return chance / 2.25F;
        } else if (Health.isPointDamage(source)) {
            return chance * 1.05F;
        } else if(Health.isDamageAllOverBody(source)) {
            return chance / 2.0F;
        } else if(source.isOf(DamageTypes.DROWN)){
            return 0;
        } else {
            return chance;
        }
    }

    public void bandage(BandageItem item) {
        this.bandageTime = 0;
        this.bandageItem = item;
        this.lastBandageItem = null;
    }

    public BandageItem unBandage() {
        this.bandageTime = 0;
        if (bandageItem != null) {
            this.lastBandageItem = bandageItem;
            bandageItem = null;
            return lastBandageItem;
        } else {
            return null;
        }
    }

    public BandageItem getBandageItem() {
        return bandageItem;
    }

    public void setBandageItem(@Nullable BandageItem item) {
        this.bandageItem = item;
    }

    public void setBandageTime(int bandageTime) {
        this.bandageTime = Math.max(bandageTime, 0);
    }

    public int getBandageTime() {
        return bandageTime;
    }

    public boolean isBandaged() {
        return this.bandageItem != null;
    }

    public float getBleeding() {
        return bleeding;
    }

    public void setBleeding(float bleeding) {
        this.bleeding = Math.max(bleeding,0);
    }

    public boolean isBleeding() {
        return this.getBleeding() != 0;
    }

    public void setInfection(boolean infection) {
        this.infection = infection;
    }

    public boolean hasInfection() {
        return infection;
    }

    public void disInfect() {
        if(new Random().nextInt(0, 3) == 0) {
            setInfection(false);
        }
    }

    public void onSleep(){
        this.heal(0.5f);
        if(this.isBandaged()) {
            this.setBleeding(this.getBleeding() - 0.3f);
        }
    }

    public Health getHealth() {
        return health;
    }

    public float getHp() {
        return hp;
    }

    public void setHp(float hp) {
        this.hp = hp;
    }

    public void setAdditionalHp(float additionalHp) {
        this.additionalHp = additionalHp;
    }

    public float getAdditionalHp() {
        return additionalHp;
    }

    public PlayerEntity getPlayer() {
        return player;
    }

    public String getId() {
        return id;
    }

    public boolean isFullHp() {
        return this.getHp() >= this.getMaxHp();
    }

    public float getPain() {
        if (this.getHp() <= 0.2f) {
            return 5f;
        } else {
            float d = (this.getMaxHp() - this.getHp());

            if (this.isBandaged())
                return d / 1.25f;
            else {
                return d;
            }
        }
    }

    public float getHpPercent() {
        return getHp() / getMaxHp();
    }

    public String getNbt() {
        JsonBuilder builder = new JsonBuilder();

        if (this.getHp() != this.getMaxHp()) {
            builder.append("hp", String.valueOf(this.getHp()));
        }
        if(this.getAdditionalHp() > 0) {
            builder.append("add_hp", String.valueOf(this.getAdditionalHp()));
        }
        if (this.isBandaged()) {
            builder.append("bandage_time", String.valueOf(this.getBandageTime()));
            builder.append("bandage_item",  String.valueOf(Item.getRawId(this.getBandageItem())));
        }
        if (this.isBleeding()) {
            builder.append("bleeding",  String.valueOf(this.getBleeding()));
        }
        if (this.hasInfection()) {
            builder.append("infection",  String.valueOf(true));
        }
        if(!builder.isEmpty()) {
            return builder.toString();
        } else {
            return null;
        }
    }

    public void readNbt(String bodyPart) {
        {
            String hp = Json.getValue(bodyPart, "hp");
            if(hp != null) {
                this.setHp(Float.parseFloat(hp));
            } else {
                this.setHp(getMaxHp());
            }
        }

        {
            String addHp = Json.getValue(bodyPart, "add_hp");
            if(addHp != null) {
               this.setAdditionalHp(Float.parseFloat(addHp));
            } else {
                this.setAdditionalHp(0);
            }
        }

        {
            String bandage_item = Json.getValue(bodyPart, "bandage_item");
            String bandage_time = Json.getValue(bodyPart, "bandage_time");
            if(bandage_item != null && bandage_time != null) {
                this.setBandageItem((BandageItem) Item.byRawId(Integer.parseInt(bandage_item)));
                this.setBandageTime(Integer.parseInt(bandage_time));
            } else {
                this.setBandageItem(null);
                this.setBandageTime(-1);
            }
        }

        {
            String bleeding = Json.getValue(bodyPart, "bleeding");
            if(bleeding != null) {
                this.setBleeding(Float.parseFloat(bleeding));
            } else {
                this.setBleeding(0);
            }
        }

        {
            String infection = Json.getValue(bodyPart, "infection");
            if(infection != null) {
                this.setInfection(Boolean.parseBoolean(infection));
            } else {
                this.setInfection(false);
            }
        }
    }

    public abstract void addEffectAmplifier(EffectAmplifiers effectAmplifiers);

    public abstract float getMaxHp();
}
