package aiven.zomboidhealthsystem.health;

import aiven.zomboidhealthsystem.Config;

import aiven.zomboidhealthsystem.ModDamageTypes;
import aiven.zomboidhealthsystem.health.bodyparts.*;
import aiven.zomboidhealthsystem.health.moodles.*;
import aiven.zomboidhealthsystem.utility.Util;

import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.JsonNodeFactory;
import tools.jackson.databind.node.ObjectNode;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

public final class Health {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static final float MAX_PLAYER_HP = 100.0F;

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

    public static final int BANDAGE_HEAL_TIME = Config.getInstance().heal_time;
    public static final int BANDAGE_BECOMES_DIRTY_AFTER = 2 * 60 * 20;
    public static final int UPDATE_FREQUENCY = Config.getInstance().update_frequency;
    public static final int MAX_SLOWNESS_AMPLIFIER = 4 - 1;

    private Player player;

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

    private float playerHp = MAX_PLAYER_HP;

    private int i = 0;

    public Health(Player player) {
        this(player, MAX_HEAD_HP, MAX_BODY_HP, MAX_ARM_HP, MAX_ARM_HP, MAX_LEG_HP, MAX_LEG_HP, MAX_FOOT_HP, MAX_FOOT_HP);
    }

    public Health(Player player,
                  float headHp, float bodyHp,
                  float leftArmHp, float rightArmHp,
                  float leftLegHp, float rightLegHp,
                  float leftFootHp, float rightFootHp
    ) {
        this.player = player;

        head = new Head(this, headHp, player);

        body = new Body(this, bodyHp, player);

        leftArm = new Arm(this, leftArmHp, player, LEFT_ARM_ID);
        rightArm = new Arm(this, rightArmHp, player, RIGHT_ARM_ID);

        leftLeg = new Leg(this, leftLegHp, player, LEFT_LEG_ID);
        rightLeg = new Leg(this, rightLegHp, player, RIGHT_LEG_ID);

        leftFoot = new Foot(this, leftFootHp, player, LEFT_FOOT_ID);
        rightFoot = new Foot(this, rightFootHp, player, RIGHT_FOOT_ID);

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

            AtomicReference<Float> weaknessAmplifier = new AtomicReference<>(0.0F);
            AtomicReference<Float> slownessAmplifier = new AtomicReference<>(0.0F);
            AtomicReference<Float> fatigueAmplifier = new AtomicReference<>(0.0F);

            for (BodyPart part : bodyParts) {
                part.addEffectAmplifiers(slownessAmplifier, fatigueAmplifier, weaknessAmplifier);
            }

            if (slownessAmplifier.get() > MAX_SLOWNESS_AMPLIFIER) {
                slownessAmplifier.set((float) MAX_SLOWNESS_AMPLIFIER);
            }

            if(slownessAmplifier.get() >= 1) {
                addMobEffect(MobEffects.MOVEMENT_SLOWDOWN, slownessAmplifier.get().intValue() - 1, 15 * 20);
            }
            if(fatigueAmplifier.get() >= 1) {
                addMobEffect(MobEffects.DIG_SLOWDOWN, fatigueAmplifier.get().intValue() - 1, 15 * 20);
            }
            if(weaknessAmplifier.get() >= 1) {
                addMobEffect(MobEffects.WEAKNESS, weaknessAmplifier.get().intValue() - 1, 15 * 20);
            }

            if (this.getPlayerHp() <= 0) {
                this.die(Util.getDamageSource(ModDamageTypes.BLEEDING,  player.level()));
            }

            this.getPain().applyEffects();
        }
    }

    public void update() {
        if (player != null && player.isAlive() && !player.isCreative() && !player.isSpectator() && !player.level().isClientSide()){
            for (BodyPart part : bodyParts) {
                part.update();
            }

            applyEffects();

            if (this.getPlayer().hasEffect(MobEffects.REGENERATION)) {
                int amplifier = this.getPlayer().getEffect(MobEffects.REGENERATION).getAmplifier();
                this.healAllParts(((float) amplifier + 1) / 100.0F * UPDATE_FREQUENCY);
                this.healPlayerHp(((float) amplifier + 1) / 100.0F * UPDATE_FREQUENCY);
            }

            if(!this.getPlayer().hasEffect(MobEffects.ABSORPTION)) {
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
        if (!player.level().isClientSide()) {
            if(!player.isAlive()) {
                return;
            }

            if(getNotNullHpBodyPartCount() == 0) {
                return;
            }

            if(amount > getMaxHpSum() + getAdditionalHpSum()) {
                for(BodyPart bodyPart : bodyParts) {
                    bodyPart.setHp(0);
                    bodyPart.setAdditionalHp(0);
                }
                die(source);
                return;
            }

            amount = amount * Config.getInstance().damage_multiplier;

            if (amount <= 0) {
                return;
            }

            if (source.getEntity() != null && source.getEntity() instanceof Zombie) {
                getZombification().onAttackByZombie(amount);
            }

            if (isPlayerHpDamage(source)) {
                damagePlayerHp(source, amount * 5);
                if (getPlayerHp() <= 0) {
                    die(source);
                }
            } else if (isPointDamage(source)) {

                float d = randomHit().damage(amount, source);
                if (d > 0.1F && this.checkImportantBodyPartsAlive(source)) {
                    damage(source, d);
                }

            } else if (isFootDamage(source)) {

                BodyPart[] orderDamageBodyParts = new BodyPart[]{
                        head,
                        leftArm, rightArm,
                        body,
                        leftLeg, rightLeg,
                        leftFoot, rightFoot
                };

                float d = amount / 2.0F;

                for (int i = 7; i != 0; i--) {
                    if (d < 0.2F) break;
                    int random = new Random().nextInt(0, 2);
                    BodyPart part = orderDamageBodyParts[i - random];
                    d = part.damage(d, source);
                }

            } else if (isItDamageToEntireBody(source)) {

                for (BodyPart part : bodyParts) {
                    part.damage(amount / getNotNullHpBodyPartCount(), source);
                }

                checkImportantBodyPartsAlive(source);

            } else {

                int random = new Random().nextInt(0, 9);
                float d = bodyParts[random].damage(amount, source);
                if (d > 0.2F && this.checkImportantBodyPartsAlive(source)) {
                    damage(source, d);
                }

            }
            applyEffects();
        }
    }

    public void damagePlayerHp(DamageSource source, float amount) {
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
            if(part.isBleeding() && (!part.isBandaged() || !part.getBandageItem().isStopBleeding())) {
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

    public void sleep(int ticks) {
        for(Moodle moodle : moodles) {
            moodle.sleep(ticks);
        }
        for(BodyPart bodyPart : bodyParts) {
            bodyPart.sleep(ticks);
        }
    }

    public boolean checkImportantBodyPartsAlive(DamageSource source) {
        boolean bl = true;
        for(BodyPart bodyPart : getBodyParts()) {
            if(bodyPart instanceof ImportartBodyPart importartBodyPart) {
                if(importartBodyPart.getHp() <= 0) {
                    bl = false;
                    break;
                }
            }
        }
        if (!bl && this.player.isAlive()) {
            die(source);
        }
        return bl;
    }

    public float getHpSum() {
        float sum = 0;
        for (BodyPart part : bodyParts) {
            sum += part.getHp();
        }
        return sum;
    }

    public float getBleedingSum() {
        float sum = 0;
        for (BodyPart part : bodyParts) {
            sum += part.getBleedingValue();
        }
        return sum;
    }

    public int getNotNullHpBodyPartCount() {
        int count = 0;
        for (BodyPart part : bodyParts) {
            if (part.getHp() > 0) count++;
        }
        return count;
    }

    public float getAdditionalHpSum() {
        float d = 0;
        for (BodyPart part : bodyParts) {
            d += part.getAdditionalHp();
        }
        return d;
    }

    public float getMaxHpSum() {
        float d = 0;
        for (BodyPart part : bodyParts) {
            d += part.getMaxHp();
        }
        return d;
    }

    public float getBodyHpPercent() {
        return getHpSum() / getMaxHpSum();
    }

    public void die(DamageSource source) {
        if (this.player.isAlive()) {
            this.player.setHealth(0);
            this.getPlayer().die(source);
        }
    }

    public boolean isFullHp() {
        for (BodyPart part : bodyParts) {
            if (!part.isFullHp()) return false;
        }
        return true;
    }

    public Player getPlayer() {
        return player;
    }

    public float getPlayerHp() {
        return playerHp;
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

    public boolean hasInfection() {
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

    public void removeMobEffect(MobEffect effect){
        if(this.getPlayer().hasEffect(effect)){
            this.getPlayer().removeEffect(effect);
        }
    }

    public void addMobEffect(MobEffect effect, int amplifier, int duration) {
        Util.addMobEffect(this.getPlayer(),effect,duration,amplifier,false,true);
    }

    public BodyPart getBodyPart(String id) {
        for(BodyPart part : bodyParts) {
            if (part.getId().equals(id)) {
                return part;
            }
        }
        return null;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    @Override
    public String toString() {
        return new String(getNbt(), StandardCharsets.UTF_8);
    }

    public byte[] getNbt() {
        ObjectNode objectNode = new ObjectNode(JsonNodeFactory.instance);

        if (this.getPlayerHp() != 100) {
            objectNode.put("player_hp", this.playerHp);
        }

        for(Moodle moodle : moodles){
            String value = moodle.getNbt();
            if(value != null) {
                objectNode.put(moodle.getId(), value);
            }
        }

        for (BodyPart part : bodyParts) {
            String nbt = part.getNbt();
            if(nbt != null) {
                objectNode.put(part.getId(), nbt);
            }
        }
        return mapper.writeValueAsBytes(objectNode);
    }

    public void readNbt(byte[] nbt) {
        ObjectNode objectNode = mapper.readTree(nbt).asObject();

        for (int i = 0; i < 8; i++) {
            BodyPart bodyPart = this.getBodyParts()[i];
            bodyPart.readNbt(objectNode.get(bodyPart.getId()) == null ? null : objectNode.get(bodyPart.getId()).asString());
        }

        for(Moodle moodle : this.moodles){
            moodle.readNbt(objectNode.get(moodle.getId()) == null ? null : objectNode.get(moodle.getId()).asString());
        }

        {
            if(objectNode.get("player_hp") != null) {
                playerHp = objectNode.get("player_hp").asFloat();
            } else {
                playerHp = MAX_PLAYER_HP;
            }
        }
    }

    public static Health parseHealth(Player player, byte[] bytes) {
        Health health = new Health(player);
        health.readNbt(bytes);
        return health;
    }

    public static Health parseHealth(byte[] bytes) {
        return parseHealth(null, bytes);
    }

    public static boolean isPlayerHpDamage(DamageSource source) {
        return source.is(DamageTypes.DROWN) || source.is(DamageTypes.INDIRECT_MAGIC) || source.is(DamageTypes.MAGIC) || source.is(DamageTypes.ON_FIRE);
    }

    public static boolean isItDamageToEntireBody(DamageSource source) {
        for (TagKey<DamageType> t : new TagKey[]{
                DamageTypeTags.IS_EXPLOSION,
                DamageTypeTags.IS_FIRE,
                DamageTypeTags.IS_FREEZING,
                DamageTypeTags.IS_LIGHTNING
        }) {
            if (source.is(t)) return true;
        }
        for (ResourceKey<DamageType> t : new ResourceKey[]{
                DamageTypes.MAGIC
        }) {
            if (source.is(t)) return true;
        }
        return false;
    }

    public boolean isFootDamage(DamageSource source) {
        return (source.is(DamageTypes.FALL) || source.is(DamageTypes.HOT_FLOOR)) && player.getPose() != Pose.SWIMMING && player.getPose() != Pose.SLEEPING;
    }

    public static boolean isPointDamage(DamageSource source) {
        for (ResourceKey<DamageType> t : new ResourceKey[]{
                DamageTypes.ARROW,
                DamageTypes.MOB_ATTACK,
                DamageTypes.MOB_PROJECTILE,
                DamageTypes.MOB_ATTACK_NO_AGGRO,
                DamageTypes.CACTUS,
                DamageTypes.PLAYER_ATTACK,
                DamageTypes.FIREBALL
        }) {
            if (source.is(t)) return true;
        }
        return false;
    }

    public static boolean once(int time) {
        return new Random().nextInt(0, time / UPDATE_FREQUENCY) == 0;
    }
}
