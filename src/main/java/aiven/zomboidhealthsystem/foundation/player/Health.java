package aiven.zomboidhealthsystem.foundation.player;

import aiven.zomboidhealthsystem.Config;
import aiven.zomboidhealthsystem.ModDamageTypes;
import aiven.zomboidhealthsystem.ModStatusEffects;
import aiven.zomboidhealthsystem.ZomboidHealthSystem;
import aiven.zomboidhealthsystem.foundation.player.moodles.*;
import aiven.zomboidhealthsystem.foundation.items.BandageItem;
import aiven.zomboidhealthsystem.foundation.utility.Util;
import aiven.zomboidhealthsystem.foundation.world.ModServer;
import aiven.zomboidhealthsystem.infrastructure.config.Json;
import aiven.zomboidhealthsystem.infrastructure.config.JsonBuilder;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class Health {
    // все числа должны быть четными, сумма == 100
    private static final int
            HEAD_CHANCE_HIT = 24,
            BODY_CHANCE_HIT = 30,
            ARMS_CHANCE_HIT = 28,
            LEGS_CHANCE_HIT = 16,
            FOOTS_CHANCE_HIT = 2;

    public static final String
            HEAD_ID = "head",
            BODY_ID = "body",
            LEFT_ARM_ID = "left_arm",
            RIGHT_ARM_ID = "right_arm",
            LEFT_LEG_ID = "left_leg",
            RIGHT_LEG_ID = "right_leg",
            LEFT_FOOT_ID = "left_foot",
            RIGHT_FOOT_ID = "right_foot";

    public static final int
            MAX_HEAD_HP = 4,
            MAX_BODY_HP = 6,
            MAX_ARM_HP = 4,
            MAX_LEG_HP = 4,
            MAX_FOOT_HP = 4;

    public static final int BANDAGE_HEAL_TIME = Config.HEAL_TIME.getValue();
    public static final int BANDAGE_BECOMES_DIRTY_AFTER = 2 * 60 * 20;
    public static final int UPDATE_FREQUENCY = ZomboidHealthSystem.UPDATE_FREQUENCY;
    public static final int MAX_SLOWNESS_AMPLIFIER = 4 - 1;
    public static final int PLAYER_FALLING_IF_AMPLIFIER = MAX_SLOWNESS_AMPLIFIER;

    private final PlayerEntity user;

    private final Head head;
    private final Body body;
    private final Arm leftArm, rightArm;
    private final Leg leftLeg, rightLeg;
    private final Foot leftFoot, rightFoot;

    private final BodyPart[] bodyParts;

    private final Pain pain;
    private final Drowsiness drowsiness;
    private final Thirst thirst;
    private final Exhaustion exhaustion;
    private final Cold cold;
    private final Wet wet;

    private final Moodle[] moodles;

    private final HungerHelper hunger;
    private final Temperature temperature;

    private boolean isDead = false;
    private float playerHp = 100;
    private boolean isCanWalk = true;

    private int i = 0;

    public Health(PlayerEntity user) {
        this(user, MAX_HEAD_HP, MAX_BODY_HP, MAX_ARM_HP, MAX_ARM_HP, MAX_LEG_HP, MAX_LEG_HP, MAX_FOOT_HP, MAX_FOOT_HP);
    }

    public Health(PlayerEntity user,
                  float headHp, float bodyHp,
                  float leftArmHp, float rightArmHp,
                  float leftLegHp, float rightLegHp,
                  float leftFootHp, float rightFootHp
    ) {
        this.user = user;

        head = new Head(this, headHp, user);

        body = new Body(this, bodyHp, user);

        leftArm = new Arm(this, leftArmHp, user, LEFT_ARM_ID);
        rightArm = new Arm(this, rightArmHp, user, RIGHT_ARM_ID);

        leftLeg = new Leg(this, leftLegHp, user, LEFT_LEG_ID);
        rightLeg = new Leg(this, rightLegHp, user, RIGHT_LEG_ID);

        leftFoot = new Foot(this, leftFootHp, user, LEFT_FOOT_ID);
        rightFoot = new Foot(this, rightFootHp, user, RIGHT_FOOT_ID);

        bodyParts = new BodyPart[]{head, body, leftArm, rightArm, leftLeg, rightLeg, leftFoot, rightFoot};

        this.pain = new Pain(this);
        this.drowsiness = new Drowsiness(this);
        this.thirst = new Thirst(this);
        this.exhaustion = new Exhaustion(this);
        this.temperature = new Temperature(this);
        this.cold = new Cold(this);
        this.wet = new Wet(this);

        this.hunger = new HungerHelper(this);

        this.moodles = new Moodle[]{pain,drowsiness,thirst,exhaustion,temperature,cold,wet};
    }


    public abstract static class BodyPart {
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

        private BodyPart(Health health, float hp, PlayerEntity player, String id) {
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
            float random = new Random().nextFloat(0, 1);
            if (random <= getBleedingChance(amount, source)) {
                this.setBleeding((this.getMaxHp() - this.getHp()) / 1.85F);
            }

            return Math.max(0, d);
        }

        protected void update() {
            if(getHp() >= getMaxHp()) {
                setBleeding(0);
            }

            if (this.isBandaged()) {
                float m = 1;
                if(this.hasInfection()) {
                    m = 3;
                }
                this.heal(bandageItem.getHealAmount() / m / BANDAGE_HEAL_TIME * (this.getPlayer().getHungerManager().getFoodLevel() / 20.0F) * UPDATE_FREQUENCY);

                this.setBandageTime(this.getBandageTime() + UPDATE_FREQUENCY);

                if (bandageItem.isDirty()) {
                    if(random(Config.INFECTION_TIME.getValue())) {
                        this.infection = true;
                    }
                }

                if (bandageItem.isStopBleeding()) {
                    if (this.isBleeding()) {
                        this.setBleeding(this.getBleeding() - (0.00015F * UPDATE_FREQUENCY));

                        if (this.getBandageTime() > BANDAGE_BECOMES_DIRTY_AFTER / (this.getBleeding() + 0.5) / bandageItem.dirtyDivisor()) {

                            this.setBandageItem(this.bandageItem.getDirtyBandageItem());
                        }
                    }
                }
            }
            if(!this.isBandaged() || !this.bandageItem.isStopBleeding()) {
                this.getHealth().damagePlayerHp(
                        Util.getDamageSource(ModDamageTypes.BLEEDING, getPlayer().getWorld()),
                        (this.getBleeding() / 30) * UPDATE_FREQUENCY);
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
            } else if (isPointDamage(source)) {
                return chance;
            } else if(isDamageAllOverBody(source)) {
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

        public Item unBandage() {
            this.bandageTime = 0;
            if (bandageItem != null) {
                this.lastBandageItem = bandageItem;
                bandageItem = null;
                return lastBandageItem;
            } else {
                return null;
            }
        }

        public Item getBandageItem() {
            return bandageItem;
        }

        private void setBandageItem(@Nullable BandageItem item) {
            this.bandageItem = item;
        }

        private void setBandageTime(int bandageTime) {
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

        protected void onSleep(){
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

        protected abstract void addEffectAmplifier(EffectAmplifiers effectAmplifiers);

        public abstract float getMaxHp();
    }


    private abstract static class ImportartBodyPart extends BodyPart {
        private ImportartBodyPart(Health health, float hp, PlayerEntity user, String name) {
            super(health, hp, user, name);
        }

        protected void nullHp() {
            this.getHealth().onDeath(new DamageSource(this.getPlayer().getWorld().getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(ModDamageTypes.SEVERE_DAMAGE)));
        }

        @Override
        public void update() {
            super.update();
            if (getHp() == 0) nullHp();
        }

        @Override
        public float damage(float amount, DamageSource source) {
            onDamage(amount);
            return super.damage(amount, source);
        }

        protected abstract void onDamage(float amount);
    }


    private static class Head extends ImportartBodyPart {
        private Head(Health health, float hp, PlayerEntity user) {
            super(health, hp, user, HEAD_ID);
        }

        @Override
        protected void onDamage(float amount) {
            if (amount >= 1) {
                getHealth().addStatusEffect(StatusEffects.BLINDNESS, 10 * 20, 0);
                this.getHealth().damagePlayerHp(new DamageSource(this.getPlayer().getWorld().getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(ModDamageTypes.BLEEDING)),amount * 5f);
            }
        }

        @Override
        protected void addEffectAmplifier(EffectAmplifiers effectAmplifiers) {

        }

        @Override
        public float getMaxHp() {
            return MAX_HEAD_HP;
        }

        @Override
        public void update() {
            super.update();
            if (this.getMaxHp() / 2 >= this.getHp()) {
                if(random(3 * 60 * 20)){
                    getHealth().addStatusEffect(StatusEffects.BLINDNESS, 10 * 20, 0);
                }
            }
        }
    }


    private static class Body extends ImportartBodyPart {
        private Body(Health health, float hp, PlayerEntity user) {
            super(health, hp, user, BODY_ID);
        }

        @Override
        protected void onDamage(float amount) {
            if (amount >= 1) {
                this.getHealth().damagePlayerHp(new DamageSource(this.getPlayer().getWorld().getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(ModDamageTypes.BLEEDING)),amount * 5f);
            }
        }

        @Override
        protected void addEffectAmplifier(EffectAmplifiers effectAmplifiers) {
            effectAmplifiers.slownessAmplifier += (getMaxHp() - getHp()) / 2;
        }

        @Override
        public float getMaxHp() {
            return MAX_BODY_HP;
        }

        @Override
        public void update() {
            super.update();
            if (this.getMaxHp() / 2 >= this.getHp()) {
                if(random(3 * 60 * 20)){
                    getHealth().addStatusEffect(StatusEffects.NAUSEA, 10 * 20, 0);
                }
            }
        }
    }


    private static class Arm extends BodyPart {
        private Arm(Health health, float hp, PlayerEntity user, String name) {
            super(health, hp, user, name);
        }

        @Override
        protected void addEffectAmplifier(EffectAmplifiers effectAmplifiers) {
            effectAmplifiers.fatigueAmplifier += (getMaxHp() - getHp()) / 3.0F;
        }

        @Override
        public float getMaxHp() {
            return MAX_ARM_HP;
        }
    }


    private static class Leg extends BodyPart {
        private Leg(Health health, float hp, PlayerEntity user, String name) {
            super(health, hp, user, name);
        }

        @Override
        protected void addEffectAmplifier(EffectAmplifiers effectAmplifiers) {
            effectAmplifiers.slownessAmplifier += (getMaxHp() - getHp()) / 2.0F;
        }

        @Override
        public float getMaxHp() {
            return MAX_LEG_HP;
        }

    }


    private static class Foot extends BodyPart {
        private Foot(Health health, float hp, PlayerEntity user, String name) {
            super(health, hp, user, name);
        }

        @Override
        protected void addEffectAmplifier(EffectAmplifiers effectAmplifiers) {
            effectAmplifiers.slownessAmplifier += (getMaxHp() - getHp()) / 2.0F;
        }

        @Override
        public float getMaxHp() {
            return MAX_FOOT_HP;
        }
    }


    private static class EffectAmplifiers {
        public float slownessAmplifier = 0;
        public float fatigueAmplifier = 0;
    }


    public void tick() {
        if (i++ >= UPDATE_FREQUENCY - 1) {
            update();
            i = 0;
        }
    }

    public void applyEffects() {
        if (this.getPlayer().isAlive() && !this.getPlayer().isCreative()) {

            EffectAmplifiers effectAmplifiers = new EffectAmplifiers();

            for (BodyPart part : bodyParts) {
                part.addEffectAmplifier(effectAmplifiers);
            }

            if (effectAmplifiers.slownessAmplifier >= PLAYER_FALLING_IF_AMPLIFIER) {
                this.getPlayer().setPose(EntityPose.SWIMMING);
                isCanWalk = false;
            } else {
                isCanWalk = true;
            }
            if (effectAmplifiers.slownessAmplifier > MAX_SLOWNESS_AMPLIFIER) {
                effectAmplifiers.slownessAmplifier = MAX_SLOWNESS_AMPLIFIER;
            }
            if(effectAmplifiers.slownessAmplifier >= 1) {
                addStatusEffect(StatusEffects.SLOWNESS, (int) effectAmplifiers.slownessAmplifier - 1, 15 * 20);
            }
            if(effectAmplifiers.fatigueAmplifier >= 1) {
                addStatusEffect(StatusEffects.MINING_FATIGUE, (int) effectAmplifiers.fatigueAmplifier - 1, 15 * 20);
                addStatusEffect(StatusEffects.WEAKNESS, (int) effectAmplifiers.fatigueAmplifier - 1, 15 * 20);
            }

            if (this.getPlayerHp() <= 0) {
                this.onDeath(Util.getDamageSource(ModDamageTypes.BLEEDING,  this.getPlayer().getWorld()));
            }

            this.getPain().applyEffects();

            if(Config.SHOW_INJURED_ICON.getValue()) {
                float sumHp = getSumOfHp();
                float maxSumHp = getMaxSumOfHp();
                if (sumHp <= maxSumHp - 2) {
                    this.addStatusEffect(ModStatusEffects.INJURED, Math.min(9, (int) ((getMaxSumOfHp() - sumHp) / 2) - 1), 15 * 20);
                } else if (this.getPlayer().hasStatusEffect(ModStatusEffects.INJURED)) {
                    this.removeStatusEffect(ModStatusEffects.INJURED);
                }
            }

            if(Config.SHOW_BLEEDING_ICON.getValue()) {
                if (isBleeding()) {
                    this.addStatusEffect(ModStatusEffects.BLEEDING, 0, 15 * 20);
                } else if (getPlayer().hasStatusEffect(ModStatusEffects.BLEEDING)) {
                    this.removeStatusEffect(ModStatusEffects.BLEEDING);
                }
            }
        }
    }

    public void update() {
        if (this.getPlayer().isAlive() && !this.getPlayer().isCreative() && !this.getPlayer().isSpectator()){
            for (BodyPart part : bodyParts) {
                part.update();
            }

            applyEffects();

            if (this.getPlayer().hasStatusEffect(StatusEffects.REGENERATION)) {
                int amplifier = this.getPlayer().getStatusEffect(StatusEffects.REGENERATION).getAmplifier();
                this.healAllParts(((float) amplifier + 1) / 100f * UPDATE_FREQUENCY);
                this.healPlayerHp(((float) amplifier + 1) / 100f * UPDATE_FREQUENCY);
            }

            if(!this.getPlayer().hasStatusEffect(StatusEffects.ABSORPTION)) {
                for(BodyPart bodyPart : bodyParts) {
                    bodyPart.setAdditionalHp(0);
                }
            }

            if (this.getPlayer().getHungerManager().getFoodLevel() >= 19 && !this.haveInfection()) {
                this.healPlayerHp(0.003f * UPDATE_FREQUENCY);
            }

            for(Moodle moodle : this.moodles){
                moodle.update();
            }

            this.getHunger().update();
        }
    }

    public void damage(DamageSource source, float amount) {
        float damageAmount = (float) Util.floor(amount, 10);

        this.getPlayer().getDamageTracker().onDamage(source,amount);

        if(damageAmount < 0) {
            return;
        }

        if (this.getPlayer().isAlive() && this.isAlive()) {
            if (source.isOf(DamageTypes.GENERIC_KILL)) {
                onDeath(source);
            } else if (source.isOf(DamageTypes.DROWN)) {
                damagePlayerHp(source, amount * 5);
                if(getPlayerHp() <= 0) {
                    onDeath(source);
                }
            } else if (isPointDamage(source)) {

                float d = randomHit().damage(damageAmount, source);
                if (d > 0.1f && this.IsImportantBodyPartsAlive(source)) {
                    damage(source, d);
                }

            } else if (source.isOf(DamageTypes.FALL) || source.isOf(DamageTypes.HOT_FLOOR) && getPlayer().getPose() != EntityPose.SWIMMING) {

                BodyPart[] orderDamageBodyParts = new BodyPart[]{
                        head,
                        leftArm,rightArm,
                        body,
                        leftLeg,rightLeg,
                        leftFoot,rightFoot
                };

                float d = damageAmount / 2f;

                for (int i = 7; i != 0; i--) {
                    if (d < 0.2f) break;
                    int random = new Random().nextInt(0, 2);
                    BodyPart part = orderDamageBodyParts[i - random];
                    d = part.damage(d, source);
                }

            } else if (isDamageAllOverBody(source)) {

                for (BodyPart part : bodyParts) {
                    part.damage(damageAmount / alivePartCount(), source);
                }

                IsImportantBodyPartsAlive(source);

            } else {

                int random = new Random().nextInt(0, 8);
                float d = bodyParts[random].damage(amount, source);
                if (d > 0.2f && this.IsImportantBodyPartsAlive(source)) {
                    damage(source, d);
                }

            }
            applyEffects();
        }
    }

    private void damagePlayerHp(DamageSource source, float amount) {
        this.getPlayer().getDamageTracker().onDamage(source,amount);
        if (amount > 0) {
            this.setPlayerHp(Math.max(this.getPlayerHp() - amount, 0));
        }
    }

    public void healPlayerHp(float amount) {
        setPlayerHp(Math.min(100, getPlayerHp() + amount));
    }

    public void healRandom(float amount) {
        if (!isFullHp()) {
            int random = new Random().nextInt(0, 8);
            float d = bodyParts[random].heal(amount);
            if (d > 0.1f) this.healRandom(d);
        }
    }

    public void healAllParts(float amount) {
        int i = 8;
        for (BodyPart part : bodyParts) {
            if (part.isFullHp()) i--;
        }
        if (i != 0) {
            for (BodyPart part : bodyParts) {
                part.heal(amount / i);
            }
        }
    }

    public boolean isBleeding() {
        for(BodyPart part : bodyParts) {
            if(part.isBleeding() && (!part.isBandaged() || !((BandageItem) part.getBandageItem()).isStopBleeding())) {
                return true;
            }
        }
        return false;
    }

    public Pain getPain() {
        return this.pain;
    }

    public Drowsiness getDrowsiness() {
        return drowsiness;
    }

    public Thirst getThirst() {
        return thirst;
    }

    public Exhaustion getExhaustion() {
        return exhaustion;
    }

    public Temperature getTemperature() {
        return temperature;
    }

    public Cold getCold() {
        return cold;
    }

    public HungerHelper getHunger() {
        return hunger;
    }

    public Wet getWet() {
        return wet;
    }

    public void stumble(float damage) {
        if (!this.getPlayer().isCrawling()) {
            int random = new Random().nextInt(6, 8);
            if (damage != 0) {
                bodyParts[random].damage(damage, null);
            }
            this.getPlayer().setPose(EntityPose.SWIMMING);
            ModServer.sendPacketDamage((ServerPlayerEntity) this.getPlayer());
            this.getPlayer().getWorld().playSound(null, this.getPlayer().getBlockPos(), SoundEvents.ENTITY_PLAYER_DEATH, SoundCategory.NEUTRAL);
            addStatusEffect(StatusEffects.BLINDNESS, 1, 3 * 20);
        }
    }

    public void stumble() {
        stumble(0.05f);
    }

    public void onSleep() {
        this.getTemperature().onSleep();
        this.getHunger().onSleep();

        for(Moodle moodle : moodles) {
            moodle.onSleep();
        }

        for(BodyPart part : bodyParts){
            part.onSleep();
        }
    }

    public boolean IsImportantBodyPartsAlive(DamageSource source) {
        boolean bl = head.getHp() <= 0 || body.getHp() <= 0;
        if (bl && this.isAlive()) {
            onDeath(source);
        }
        return bl;
    }

    public float getSumOfHp() {
        float sum = 0;
        for (BodyPart part : bodyParts) {
            sum += part.getHp();
        }
        return sum;
    }

    public float getSumBleeding() {
        float sum = 0;
        for (BodyPart part : bodyParts) {
            sum += part.getBleeding();
        }
        return sum;
    }

    public int alivePartCount() {
        int count = 0;
        for (BodyPart part : bodyParts) {
            if (part.getHp() > 0) count++;
        }
        return count;
    }

    public float getMaxSumOfHp() {
        float d = 0;
        for (BodyPart part : bodyParts) {
            d += part.getMaxHp();
        }
        return d;
    }

    public float getBodyHpPercent() {
        return getSumOfHp() / getMaxSumOfHp();
    }

    public void onDeath(DamageSource source) {
        if (this.isAlive()) {
            if(source != null) {
                this.getPlayer().getDamageTracker().onDamage(source, 0);
            }
            isDead = true;
            this.getPlayer().setHealth(0);
            this.getPlayer().onDeath(source);
        }
    }

    public boolean isFullHp() {
        for (BodyPart part : bodyParts) {
            if (!part.isFullHp()) return false;
        }
        return true;
    }

    public PlayerEntity getPlayer() {
        return user;
    }

    public float getPlayerHp() {
        return playerHp;
    }

    public boolean isAlive() {
        return !isDead && this.getPlayer().isAlive();
    }

    public boolean isCanWalk() {
        return isCanWalk;
    }

    public void setPlayerHp(float playerHp) {
        this.playerHp = playerHp;
    }

    public BodyPart[] getBodyParts() {
        return bodyParts;
    }

    public boolean haveInfection() {
        for (BodyPart part : bodyParts) {
            if (part.hasInfection()) return true;
        }
        return false;
    }

    public BodyPart randomHit() {
        int random = new Random().nextInt(0, 101);

        if (random <= HEAD_CHANCE_HIT)
            return head;
        else if (random <= BODY_CHANCE_HIT + HEAD_CHANCE_HIT)
            return body;
        else if (random <= HEAD_CHANCE_HIT + BODY_CHANCE_HIT + (ARMS_CHANCE_HIT / 2))
            return leftArm;
        else if (random <= HEAD_CHANCE_HIT + BODY_CHANCE_HIT + ARMS_CHANCE_HIT)
            return rightArm;
        else if (random <= HEAD_CHANCE_HIT + BODY_CHANCE_HIT + ARMS_CHANCE_HIT + (LEGS_CHANCE_HIT / 2))
            return leftLeg;
        else if (random <= HEAD_CHANCE_HIT + BODY_CHANCE_HIT + ARMS_CHANCE_HIT + LEGS_CHANCE_HIT)
            return rightLeg;
        else if (random <= HEAD_CHANCE_HIT + BODY_CHANCE_HIT + ARMS_CHANCE_HIT + LEGS_CHANCE_HIT + (FOOTS_CHANCE_HIT / 2))
            return leftFoot;
        else
            return rightFoot;
    }

    public void removeStatusEffect(StatusEffect effect){
        if(this.getPlayer().hasStatusEffect(effect)){
            this.getPlayer().removeStatusEffect(effect);
        }
    }

    public void addStatusEffect(StatusEffect effect, int amplifier, int duration) {
        Util.addStatusEffect(this.getPlayer(),effect,duration,amplifier);
    }

    public BodyPart getBodyPart(String id) {
        for(BodyPart part : bodyParts) {
            if (part.getId().equals(id)) {
                return part;
            }
        }
        return null;
    }

    public String toString() {
        JsonBuilder healthBuilder = new JsonBuilder();

        if (this.getPlayerHp() != 100) {
            healthBuilder.append("player_hp", String.valueOf(this.playerHp));
        }

        for(Moodle moodle : moodles){
            String value = moodle.getNbt();
            if(value != null) {
                healthBuilder.append(moodle.getId(), value);
            }
        }

        for (BodyPart part : bodyParts) {
            JsonBuilder bodyPartBuilder = new JsonBuilder();

            if (part.getHp() != part.getMaxHp()) {
                bodyPartBuilder.append("hp", String.valueOf(part.getHp()));
            }
            if(part.getAdditionalHp() > 0) {
                bodyPartBuilder.append("add_hp", String.valueOf(part.getAdditionalHp()));
            }
            if (part.isBandaged()) {
                bodyPartBuilder.append("bandage_time", String.valueOf(part.getBandageTime()));
                bodyPartBuilder.append("bandage_item",  String.valueOf(Item.getRawId(part.getBandageItem())));
            }
            if (part.isBleeding()) {
                bodyPartBuilder.append("bleeding",  String.valueOf(part.getBleeding()));
            }
            if (part.hasInfection()) {
                bodyPartBuilder.append("infection",  String.valueOf(true));
            }
            if(!bodyPartBuilder.isEmpty()) {
                healthBuilder.append(part.getId(), bodyPartBuilder.toString());
            }
        }
        return healthBuilder.toString();
    }

    public static Health parseHealth(PlayerEntity user, String value) {
        Health health = new Health(user);

        for (int i = 0; i < 8; i++) {
            BodyPart bodyPart = health.getBodyParts()[i];
            String bodyPartValue = Json.getValue(value, bodyPart.getId());
            if(bodyPartValue != null) {
                parseBodyPart(bodyPartValue, bodyPart);
            }
        }

        for(Moodle moodle : health.moodles){
            String conditionValue = Json.getValue(value, moodle.getId());
            if(conditionValue != null) {
                moodle.readNbt(conditionValue);
            }
        }

        if (health.head.getHp() <= 0 || health.body.getHp() <= 0) {
            health.isDead = true;
        }

        {
            String player_hp = Json.getValue(value, "player_hp");
            if (player_hp != null) {
                health.setPlayerHp(Float.parseFloat(player_hp));
            } else {
                health.setPlayerHp(100);
            }
        }

        return health;
    }

    private static void parseBodyPart(String value, BodyPart bodyPart){
        {
            String hp = Json.getValue(value, "hp");
            if(hp != null) {
                bodyPart.setHp(Float.parseFloat(hp));
            }
        }

        {
            String addHp = Json.getValue(value, "add_hp");
            if(addHp != null) {
                bodyPart.setAdditionalHp(Float.parseFloat(addHp));
            }
        }

        {
            String bandage_item = Json.getValue(value, "bandage_item");
            String bandage_time = Json.getValue(value, "bandage_time");
            if(bandage_item != null && bandage_time != null) {
                bodyPart.setBandageItem((BandageItem) Item.byRawId(Integer.parseInt(bandage_item)));
                bodyPart.setBandageTime(Integer.parseInt(bandage_time));
            }
        }

        {
            String bleeding = Json.getValue(value, "bleeding");
            if(bleeding != null) {
                bodyPart.setBleeding(Float.parseFloat(bleeding));
            }
        }

        {
            String infection = Json.getValue(value, "infection");
            if(infection != null) {
                bodyPart.setInfection(Boolean.parseBoolean(infection));
            }
        }
    }

    private static boolean isDamageAllOverBody(DamageSource source) {
        for (TagKey<DamageType> t : new TagKey[]{
                DamageTypeTags.IS_EXPLOSION,
                DamageTypeTags.BYPASSES_EFFECTS,
                DamageTypeTags.IS_FIRE,
                DamageTypeTags.IS_FREEZING,
                DamageTypeTags.IS_LIGHTNING
        }) {
            if (source.isIn(t)) return true;
        }
        for (RegistryKey<DamageType> t : new RegistryKey[]{
                DamageTypes.MAGIC
        }) {
            if (source.isOf(t)) return true;
        }
        return false;
    }

    private static boolean isPointDamage(DamageSource source) {
        for (RegistryKey<DamageType> t : new RegistryKey[]{
                DamageTypes.ARROW,
                DamageTypes.MOB_ATTACK,
                DamageTypes.MOB_PROJECTILE,
                DamageTypes.MOB_ATTACK_NO_AGGRO,
                DamageTypes.CACTUS,
                DamageTypes.PLAYER_ATTACK,
                DamageTypes.FIREBALL
        }) {
            if (source.isOf(t)) return true;
        }
        return false;
    }

    public static boolean random(int time) {
        return new Random().nextInt(0, time / UPDATE_FREQUENCY) == 0;
    }
}