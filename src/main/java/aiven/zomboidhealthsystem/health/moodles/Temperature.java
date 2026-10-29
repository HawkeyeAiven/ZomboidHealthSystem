package aiven.zomboidhealthsystem.health.moodles;

import aiven.zomboidhealthsystem.*;
import aiven.zomboidhealthsystem.health.Health;
import aiven.zomboidhealthsystem.utility.Util;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

public final class Temperature extends SimpleMoodle {
    public static final float MIN_COMFORTABLE_TEMPERATURE = Config.getInstance().min_comfortable_temp;
    public static final float MAX_COMFORTABLE_TEMPERATURE = Config.getInstance().max_comfortable_temp;

    public static final float AVERAGE_TEMPERATURE_BODY = 37.0F;

    public static final float MIN_TEMPERATURE_BODY = AVERAGE_TEMPERATURE_BODY - 6;
    public static final float MAX_TEMPERATURE_BODY = AVERAGE_TEMPERATURE_BODY + 6;

    private boolean isFeelingCold = false;
    private boolean isFeelingHot = false;

    private float heat = 0;
    private float heatFromCampfire = 0;
    private float heatFromFurnace = 0;


    public Temperature(Health health) {
        super(health);
        setAmount(AVERAGE_TEMPERATURE_BODY);
    }

    @Override
    public String getId() {
        return "temperature";
    }

    @Override
    protected float minValue() {
        return Float.MIN_VALUE;
    }

    @Override
    protected float defaultValue() {
        return AVERAGE_TEMPERATURE_BODY;
    }

    @Override
    public void update() {
        float deltaTemp = Math.abs(this.getAmount() - AVERAGE_TEMPERATURE_BODY);
        boolean isOverWorld = isOverWorld();
        if(Config.getInstance().temperature) {
            Thirst thirst = getHealth().getThirst();
            Hunger hunger = getHealth().getHunger();

            thirst.setMultiplier(this, 1.0F);
            hunger.setMultiplier(this, 1.0F);
            this.getHealth().getExhaustion().setMultiplier(this, 1.0F);

            float temperature = getPerceivedTemperature();
            float minComfortableTemp = getMinComfortableTemperature();
            float maxComfortableTemp = getMaxComfortableTemperature();

            if (temperature < minComfortableTemp && isOverWorld) {
                this.amount -= (((minComfortableTemp - temperature) / 2)
                        / 10000
                        * Math.max(1, (hunger.getAmount() / 2.5F) + 1)
                        * Config.getInstance().temperature_multiplier
                        * Health.UPDATE_FREQUENCY
                );
                isFeelingCold = true;
                isFeelingHot = false;
            } else if (temperature > maxComfortableTemp && isOverWorld) {
                this.amount += (((temperature - maxComfortableTemp) / 2)
                        / 10000
                        * Math.max(1, thirst.getAmount())
                        * Config.getInstance().temperature_multiplier
                        * Health.UPDATE_FREQUENCY
                );
                isFeelingHot = true;
                isFeelingCold = false;
            } else {
                isFeelingHot = false;
                isFeelingCold = false;
                if (this.amount < AVERAGE_TEMPERATURE_BODY - 0.1F) {
                    this.amount += 0.003F
                            * Config.getInstance().temperature_multiplier
                            * Health.UPDATE_FREQUENCY;
                    hunger.setMultiplier(this, 1.33F);
                } else if (this.amount > AVERAGE_TEMPERATURE_BODY + 0.1F) {
                    this.amount -= 0.003F
                            / ((thirst.getAmount() + 1.0F) * 1.5F)
                            * Config.getInstance().temperature_multiplier
                            * Health.UPDATE_FREQUENCY;
                    thirst.setMultiplier(this, 1.33F);
                }
            }

            if (deltaTemp >= 1.5F) {
                this.getHealth().getExhaustion().setMultiplier(this, deltaTemp / 1.5F);
                if (deltaTemp >= 3.0F) {
                    this.getHealth().addMobEffect(MobEffects.MOVEMENT_SLOWDOWN, (int) deltaTemp / 2 - 1, 15 * 20);
                    this.getHealth().addMobEffect(MobEffects.DIG_SLOWDOWN, (int) deltaTemp / 2 - 1, 15 * 20);
                    this.getHealth().addMobEffect(MobEffects.WEAKNESS, (int) deltaTemp / 2 - 1, 15 * 20);
                    if (once(2 * 60 * 20)) {
                        this.getHealth().addMobEffect(MobEffects.CONFUSION, 0, 10 * 20);
                    }
                    if (once(2 * 60 * 20)) {
                        this.getHealth().addMobEffect(MobEffects.DARKNESS, 0, 5 * 20);
                    }
                    if (deltaTemp >= 4.5F) {
                        if (once(60 * 20)) {
                            this.getHealth().addMobEffect(MobEffects.CONFUSION, 0, 10 * 20);
                        }
                        if (once(60 * 20)) {
                            this.getHealth().addMobEffect(MobEffects.DARKNESS, 0, 5 * 20);
                        }
                    }
                }
            }

            if (this.amount < MIN_TEMPERATURE_BODY) {
                this.getHealth().die(Util.getDamageSource(ModDamageTypes.HYPOTHERMIA, getPlayer().level()));
            }
            if (this.amount > MAX_TEMPERATURE_BODY) {
                this.getHealth().die(Util.getDamageSource(ModDamageTypes.HYPERTHERMIA, getPlayer().level()));
            }
        }

        heat = 0;
        heatFromCampfire = 0;
        heatFromFurnace = 0;
    }

    @Override
    public ResourceLocation getMoodleIconTexture() {
        if(this.getAmount() > AVERAGE_TEMPERATURE_BODY + 1.5F) {
            return ZomboidHealthSystem.getResourceLocation("textures/moodle/moodle_icon_hyperthermia.png");
        } else if(this.getAmount() < AVERAGE_TEMPERATURE_BODY - 1.5F) {
            return ZomboidHealthSystem.getResourceLocation("textures/moodle/moodle_icon_hypothermia.png");
        } else {
            return null;
        }
    }

    @Override
    public String getMoodleIconText() {
        if (this.getAmount() > AVERAGE_TEMPERATURE_BODY + 1.5F) {
            return Component.translatable(ZomboidHealthSystem.ID + ".moodle.hyperthermia").getString() + " " + getAmplifier();
        } else if (this.getAmount() < AVERAGE_TEMPERATURE_BODY - 1.5F) {
            return Component.translatable(ZomboidHealthSystem.ID + ".moodle.hypothermia").getString() + " " + getAmplifier();
        } else {
            return null;
        }
    }

    @Override
    public int getAmplifier() {
        return (int) (Math.abs(AVERAGE_TEMPERATURE_BODY - getAmount()) / 1.5F);
    }

    @Override
    public void sleep(int sumTicks) {

    }

    public float getPerceivedTemperature() {
        return getTemperatureAtUserPos() + getHeat() + getHeatFromHeatSources() + getHeatFromArmor();
    }

    public float getTemperatureAtUserPos() {
       return getTemperatureAtPos(getPlayer().blockPosition());
    }

    public float getTemperatureAtPos(BlockPos pos) {
        float temperatureBiome = getLevel().getBiome(pos).value().getBaseTemperature();
        float height = pos.getY() - 63;
        float t = 20 - (height / 25);
        if(temperatureBiome > 0.5f){
            t += 10 * temperatureBiome;
        } else if (temperatureBiome < 0.15f) {
            temperatureBiome -= 0.15f;
            temperatureBiome *= 3;
            t += 15 * temperatureBiome;
        }

        return t;
    }

    public float getHeatFromHeatSources() {
        return getHeatFromCampfire() + getHeatFromFurnace() + getHeatFromLava();
    }

    private float getMinComfortableTemperature() {
        Health health = getHealth();
        Thirst thirst = health.getThirst();
        float hpPercent = (float) Math.sqrt(health.getBodyHpPercent());
        float foodLvl = Math.max(getHealth().getHunger().getAmount(), 1);
        foodLvl = (float) (Math.cbrt(Math.cbrt(foodLvl)));
        return MIN_COMFORTABLE_TEMPERATURE / hpPercent * foodLvl + ((Math.max(thirst.getAmount() - 1,0)) * 2);
    }

    private float getMaxComfortableTemperature() {
        Health health = getHealth();
        Thirst thirst = health.getThirst();
        return MAX_COMFORTABLE_TEMPERATURE - ((Math.max(thirst.getAmount() - 1,0)) * 2);
    }

    public void heal(int divisor){
        setTemperature(((AVERAGE_TEMPERATURE_BODY * divisor) + this.getAmount()) / (2 + (divisor - 1)));
    }

    public void cool(float amount){
        if(this.getAmount() - amount > AVERAGE_TEMPERATURE_BODY) {
            this.amount -= amount;
        } else {
            this.amount -= amount / 2;
        }
    }

    public void setHeat(float heat) {
        this.heat = heat;
    }

    public void setHeatFromCampfire(float heatFromCampfire) {
        this.heatFromCampfire = heatFromCampfire;
    }

    public void setHeatFromFurnace(float heatFromFurnace) {
        this.heatFromFurnace = heatFromFurnace;
    }

    public float getHeatFromCampfire() {
        return heatFromCampfire;
    }

    public float getHeatFromFurnace() {
        return heatFromFurnace;
    }

    public void setTemperature(float temperature) {
        this.amount = temperature;
    }

    public void addHeat(float heat) {
        this.setHeat(getHeat() + heat);
    }

    public float getHeat() {
        return heat;
    }

    public float getHeatFromLava() {
        float distance = Util.getDistance(getLevel(), Blocks.LAVA, getPlayer().blockPosition(), 5);
        if(distance == -1 || distance >= 5) {
            return 0;
        }
        return Config.getInstance().max_heat_from_block - (distance * Config.getInstance().max_heat_from_block / 5);
    }

    public boolean isFeelingCold() {
        return isFeelingCold;
    }

    public boolean isFeelingHot() {
        return isFeelingHot;
    }

    private float getHeatFromArmor(){
        Player player = this.getHealth().getPlayer();
        float heat = 0;
        for(ItemStack stack : player.getArmorSlots()){
            Item item = stack.getItem();
            if(!item.equals(Items.AIR)){
                if(item.equals(Items.LEATHER_HELMET)){
                    heat += Config.getInstance().heat_from_leather_helmet;
                } else if(item.equals(Items.LEATHER_CHESTPLATE)){
                    heat += Config.getInstance().heat_from_leather_chestplate;
                } else if(item.equals(Items.LEATHER_LEGGINGS)){
                    heat += Config.getInstance().heat_from_leather_leggings;
                } else if(item.equals(Items.LEATHER_BOOTS)){
                    heat += Config.getInstance().heat_from_leather_boots;
                } else if(item.equals(ModItems.WOOL_HELMET)){
                    heat += Config.getInstance().heat_from_wool_helmet;
                } else if(item.equals(ModItems.WOOL_CHESTPLATE)){
                    heat += Config.getInstance().heat_from_wool_chestplate;
                } else if(item.equals(ModItems.WOOL_LEGGINGS)){
                    heat += Config.getInstance().heat_from_wool_leggings;
                } else if(item.equals(ModItems.WOOL_BOOTS)){
                    heat += Config.getInstance().heat_from_wool_boots;
                } else {
                    heat += Config.getInstance().heat_from_other_armor;
                }
            }
        }
        return heat;
    }
}