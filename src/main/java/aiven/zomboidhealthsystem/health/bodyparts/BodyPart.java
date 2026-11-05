package aiven.zomboidhealthsystem.health.bodyparts;

import aiven.zomboidhealthsystem.Config;
import aiven.zomboidhealthsystem.item.AbstractBandageItem;
import aiven.zomboidhealthsystem.health.Health;
import aiven.zomboidhealthsystem.utility.Util;

import com.github.HawkeyeAiven.json5.ObjectMapper;

import net.minecraft.stats.Stats;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;

import org.jetbrains.annotations.Nullable;


import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public abstract class BodyPart {
    private static final ObjectMapper mapper = new ObjectMapper();

    protected final Health health;
    protected float hp;
    protected float additionalHp;
    protected final Player player;
    protected final String id;
    protected int bandageTime = 0;
    protected AbstractBandageItem bandageItem;
    protected float bleedingValue = 0;
    protected boolean hasInfection = false;

    public BodyPart(Health health, float hp, Player player, String id) {
        this.health = health;
        this.hp = hp;
        this.player = player;
        this.id = id;
    }

    public abstract void addEffectAmplifiers(AtomicReference<Float> slowness, AtomicReference<Float> fatigue, AtomicReference<Float> weakness);

    public abstract float getMaxHp();

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
        if (getAdditionalHp() > 0) {
            float g = amount;
            amount = amount - this.additionalHp;
            setAdditionalHp(Math.max(this.additionalHp - amount, 0));

            if(amount <= 0) {
                this.player.awardStat(Stats.DAMAGE_ABSORBED, Math.round(g * 10.0F));
            }
        }

        if (amount <= 0) {
            return 0;
        }

        float d = amount - this.hp;
        setHp(this.hp - amount);
        if (Util.random(getBleedingChance(hp + amount, hp, source))) {
            this.setBleedingValue((this.getMaxHp() - this.getHp()) / 1.85F);
        }
        return Math.max(0, d);
    }

    public void update(float tickDelta) {
        if(tickDelta != 0) {
            if (getHp() >= getMaxHp()) {
                setBleedingValue(0);
            }

            if (this.isBandaged()) {
                this.heal(
                        Config.getInstance().heal_value
                                * bandageItem.getHealMultiplier()
                                / (this.hasInfection() ? 3 : 1)
                                * (this.getHealth().getHunger().getValue() < 0 ? 1.25F : 1.0F)
                                / (float) Math.sqrt(Math.max(getHealth().getHunger().getValue(), 1))
                                * tickDelta
                );

                this.bandageTime += tickDelta;

                if (bandageItem.isDirty() && isBleeding()) {
                    if (Util.random(Config.getInstance().infection_chance * tickDelta)) {
                        this.hasInfection = true;
                    }
                }

                if (bandageItem.isStopBleeding()) {
                    if (this.isBleeding()) {
                        this.setBleedingValue(this.getBleedingValue() - (0.00015F * tickDelta));

                        if (this.getBandageTime() > Health.BANDAGE_BECOMES_DIRTY_AFTER / (this.getBleedingValue() + 0.5F)) {

                            this.bandageItem = this.bandageItem.getDirtyBandageItem();
                        }
                    }
                }
            }
            if (!this.isBandaged() || !this.bandageItem.isStopBleeding()) {
                this.health.getBlood().addValue(-this.getBleedingValue() / 30 * tickDelta);
            }
        }
    }

    public void bandage(AbstractBandageItem item) {
        this.bandageTime = 0;
        this.bandageItem = item;
    }

    public AbstractBandageItem unBandage() {
        this.bandageTime = 0;
        if (bandageItem != null) {
            AbstractBandageItem lastBandageItem = bandageItem;
            bandageItem = null;
            return lastBandageItem;
        } else {
            return null;
        }
    }

    protected float getBleedingChance(float oldHp, float newHp, DamageSource source) {
        Objects.requireNonNull(source, "source == null");

        float damage = oldHp - newHp;

        if (newHp <= 1) {
            return 1.0F;
        }

        float chance = Config.getInstance().average_bleeding_chance * damage;
        chance *= Math.max(1, (this.getMaxHp() - newHp) * 0.875F);

        if ((source.is(DamageTypes.FALL) || source.is(DamageTypes.HOT_FLOOR)) && !getHealth().getPlayer().getPose().equals(Pose.SWIMMING)) {
            return chance / 2.25F;
        } else if (Health.isPointDamage(source)) {
            return chance * 1.05F;
        } else if (Health.isItDamageToEntireBody(source)) {
            return chance / 2.0F;
        } else {
            return chance;
        }
    }

    public void disInfect() {
        if (Util.random(0.33F)) {
            hasInfection = false;
        }
    }

    public void sleep(int ticks) {
        this.heal(0.75F / 8000F * ticks);
        if (this.isBandaged()) {
            this.setBleedingValue(this.getBleedingValue() - (1.0F / 8000 * ticks));
        }
    }

    public AbstractBandageItem getBandageItem() {
        return bandageItem;
    }

    protected void setBandageItem(@Nullable AbstractBandageItem item) {
        this.bandageItem = item;
    }

    public int getBandageTime() {
        return bandageTime;
    }

    public boolean isBandaged() {
        return this.bandageItem != null;
    }

    public float getBleedingValue() {
        return bleedingValue;
    }

    protected void setBleedingValue(float bleedingValue) {
        this.bleedingValue = Math.max(bleedingValue, 0);
    }

    public boolean isBleeding() {
        return this.getBleedingValue() != 0;
    }

    public boolean hasInfection() {
        return hasInfection;
    }

    public Health getHealth() {
        return health;
    }

    public float getHp() {
        return hp;
    }

    public void setHp(float hp) {
        this.hp = Util.clamp(hp, 0.0F, getMaxHp());
    }

    public void setAdditionalHp(float additionalHp) {
        this.additionalHp = additionalHp;
    }

    public float getAdditionalHp() {
        return additionalHp;
    }

    public Player getPlayer() {
        return player;
    }

    public String getId() {
        return id;
    }

    public boolean isFullHp() {
        return this.getHp() >= this.getMaxHp();
    }

    public float getHpPercent() {
        return getHp() / getMaxHp();
    }

    public String getNbt() {
        Nbt nbt = new Nbt();
        nbt.hp = hp;
        nbt.additionalHp = additionalHp;
        nbt.bandageTime = bandageTime;
        nbt.bandageItem = bandageItem;
        nbt.bleedingValue = bleedingValue;
        nbt.hasInfection = hasInfection;
        try {
            return mapper.write(nbt);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void readNbt(String nbtString) {
        if(nbtString != null) {
            Nbt nbt = mapper.read(nbtString, Nbt.class);
            hp = nbt.hp;
            additionalHp = nbt.additionalHp;
            bandageTime = nbt.bandageTime;
            bandageItem = nbt.bandageItem;
            bleedingValue = nbt.bleedingValue;
            hasInfection = nbt.hasInfection;
        } else {
            reset();
        }
    }

    public void reset() {
        this.hasInfection = false;
        this.hp = getMaxHp();
        this.bleedingValue = 0;
        this.bandageItem = null;
        this.additionalHp = 0;
        this.bandageTime = 0;
    }

    public static class Nbt {
        public Float hp;
        public Float additionalHp;
        public Integer bandageTime = 0;
        public AbstractBandageItem bandageItem;
        public Float bleedingValue;
        public Boolean hasInfection;
    }
}
