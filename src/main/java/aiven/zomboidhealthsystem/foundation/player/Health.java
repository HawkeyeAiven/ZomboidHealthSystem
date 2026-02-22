package aiven.zomboidhealthsystem.foundation.player;

import aiven.zomboidhealthsystem.Config;
import aiven.zomboidhealthsystem.ModDamageTypes;
import aiven.zomboidhealthsystem.ZomboidHealthSystem;
import aiven.zomboidhealthsystem.foundation.player.bodyparts.*;
import aiven.zomboidhealthsystem.foundation.player.moodles.*;
import aiven.zomboidhealthsystem.foundation.item.BandageItem;
import aiven.zomboidhealthsystem.foundation.utility.EffectAmplifiers;
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
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

import java.util.ArrayList;
import java.util.List;
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
    private final Hunger hunger;
    private final Wind wind;
    private final Drunkenness drunkenness;
    private final Zombification zombification;

    private final Moodle[] moodles;
    private final Temperature temperature;

    private boolean isDead = false;
    private float playerHp = 100;

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
        this.hunger = new Hunger(this);
        this.wind = new Wind(this);
        this.drunkenness = new Drunkenness(this);
        this.zombification = new Zombification(this);

        this.moodles = new Moodle[]{pain,drowsiness,thirst,exhaustion,temperature,cold,wet,hunger,wind,drunkenness,zombification};
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

            if(!canPlayerWalk()) {
                this.getPlayer().setPose(EntityPose.SWIMMING);
            }

            if (effectAmplifiers.slownessAmplifier > MAX_SLOWNESS_AMPLIFIER) {
                effectAmplifiers.slownessAmplifier = MAX_SLOWNESS_AMPLIFIER;
            }
            if(effectAmplifiers.slownessAmplifier >= 1) {
                addStatusEffect(StatusEffects.SLOWNESS, (int) effectAmplifiers.slownessAmplifier - 1, 15 * 20);
            }
            if(effectAmplifiers.fatigueAmplifier >= 1) {
                addStatusEffect(StatusEffects.MINING_FATIGUE, (int) effectAmplifiers.fatigueAmplifier - 1, 15 * 20);
            }
            if(effectAmplifiers.weaknessAmplifier >= 1) {
                addStatusEffect(StatusEffects.WEAKNESS, (int) effectAmplifiers.weaknessAmplifier - 1, 15 * 20);
            }

            if (this.getPlayerHp() <= 0) {
                this.onDeath(Util.getDamageSource(ModDamageTypes.BLEEDING,  this.getPlayer().getWorld()));
            }

            this.getPain().applyEffects();
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
                this.healAllParts(((float) amplifier + 1) / 100.0F * UPDATE_FREQUENCY);
                this.healPlayerHp(((float) amplifier + 1) / 100.0F * UPDATE_FREQUENCY);
            }

            if(!this.getPlayer().hasStatusEffect(StatusEffects.ABSORPTION)) {
                for(BodyPart bodyPart : bodyParts) {
                    bodyPart.setAdditionalHp(0);
                }
            }

            if(getHunger().getAmount() < 2) {
                this.healPlayerHp(0.003F * (getHunger().getAmount() < 0 ? 1.25F : 1.0F) * UPDATE_FREQUENCY);
            }

            for(Moodle moodle : this.moodles){
                moodle.update();
            }
        }
    }

    public void damage(DamageSource source, float amount) {
        if(source.getAttacker() != null && source.getAttacker() instanceof ZombieEntity) {
            getZombification().onAttackByZombie(amount);
        }

        float damageAmount = (float) Util.floor(amount, 10);

        this.getPlayer().getDamageTracker().onDamage(source,amount);

        if(damageAmount < 0) {
            return;
        }

        if (this.getPlayer().isAlive() && this.isAlive()) {
            if (source.isOf(DamageTypes.GENERIC_KILL)) {
                onDeath(source);
            } else if (source.isOf(DamageTypes.DROWN) || source.isOf(DamageTypes.INDIRECT_MAGIC) || source.isOf(DamageTypes.MAGIC)) {
                damagePlayerHp(source, amount * 5);
                if(getPlayerHp() <= 0) {
                    onDeath(source);
                }
            } else if (isPointDamage(source)) {

                float d = randomHit().damage(damageAmount, source);
                if (d > 0.1F && this.IsImportantBodyPartsAlive(source)) {
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

                float d = damageAmount / 2.0F;

                for (int i = 7; i != 0; i--) {
                    if (d < 0.2F) break;
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
                if (d > 0.2F && this.IsImportantBodyPartsAlive(source)) {
                    damage(source, d);
                }

            }
            applyEffects();
        }
    }

    public void damagePlayerHp(DamageSource source, float amount) {
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

    public Hunger getHunger() {
        return hunger;
    }

    public Wet getWet() {
        return wet;
    }

    public Wind getWind() {
        return wind;
    }

    public Drunkenness getDrunkenness() {
        return drunkenness;
    }

    public Zombification getZombification() {
        return zombification;
    }

    public Head getHead() {
        return head;
    }

    public Body getBody() {
        return body;
    }

    public Arm getLeftArm() {
        return leftArm;
    }

    public Arm getRightArm() {
        return rightArm;
    }

    public Leg getLeftLeg() {
        return leftLeg;
    }

    public Leg getRightLeg() {
        return rightLeg;
    }

    public Foot getLeftFoot() {
        return leftFoot;
    }

    public Foot getRightFoot() {
        return rightFoot;
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
        stumble(0.05F);
    }

    public void sleep(int ticks) {
        for(Moodle moodle : moodles) {
            moodle.sleep(ticks);
        }
        for(BodyPart bodyPart : bodyParts) {
            bodyPart.sleep(ticks);
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

    public boolean canPlayerWalk() {
        float legsHp = leftLeg.getHp() + rightLeg.getHp() + leftFoot.getHp() + rightFoot.getHp();
        float legsMaxHp = leftLeg.getMaxHp() + rightLeg.getMaxHp() + leftFoot.getMaxHp() + rightFoot.getMaxHp();
        float percent = legsHp / legsMaxHp;
        return percent > 0.60F && getExhaustion().canPlayerWalk();
    }

    public void setPlayerHp(float playerHp) {
        this.playerHp = playerHp;
    }

    public BodyPart[] getBodyParts() {
        return bodyParts;
    }

    public Moodle[] getMoodles() {
        return moodles;
    }

    public Moodle[] getSortMoodleArray() {
        ArrayList<Moodle> moodles = new ArrayList<>(List.of(getMoodles()));
        Moodle[] list = new Moodle[moodles.size()];
        int index = 0;
        int max;
        Moodle moodle;
        while (index < list.length) {
            max = -9999;
            moodle = null;
            for(Moodle moodle1 : moodles) {
                if(moodle1.getAmplifier() > max) {
                    max = moodle1.getAmplifier();
                    moodle = moodle1;
                }
            }
            moodles.remove(moodle);
            list[index] = moodle;
            index++;
        }

        return list;
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
            String nbt = part.getNbt();
            if(nbt != null) {
                healthBuilder.append(part.getId(), nbt);
            }
        }
        return healthBuilder.toString();
    }

    public void set(String health) {
        for (int i = 0; i < 8; i++) {
            BodyPart bodyPart = this.getBodyParts()[i];
            String bodyPartValue = Json.getValue(health, bodyPart.getId());
            bodyPart.readNbt(bodyPartValue);
        }

        for(Moodle moodle : this.moodles){
            String moodleValue = Json.getValue(health, moodle.getId());
            moodle.readNbt(moodleValue);
        }

        if (this.head.getHp() <= 0 || this.body.getHp() <= 0) {
            this.isDead = true;
        }

        {
            String player_hp = Json.getValue(health, "player_hp");
            if (player_hp != null) {
                this.setPlayerHp(Float.parseFloat(player_hp));
            } else {
                this.setPlayerHp(100);
            }
        }
    }

    public static Health parseHealth(PlayerEntity user, String value) {
        Health health = new Health(user);
        health.set(value);
        return health;
    }

    public static boolean isDamageAllOverBody(DamageSource source) {
        for (TagKey<DamageType> t : new TagKey[]{
                DamageTypeTags.IS_EXPLOSION,
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

    public static boolean isPointDamage(DamageSource source) {
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

    public static boolean once(int time) {
        return ZomboidHealthSystem.RANDOM.nextInt(0, time / UPDATE_FREQUENCY) == 0;
    }
}