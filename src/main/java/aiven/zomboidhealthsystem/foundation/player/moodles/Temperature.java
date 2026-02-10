package aiven.zomboidhealthsystem.foundation.player.moodles;

import aiven.zomboidhealthsystem.Config;
import aiven.zomboidhealthsystem.ModDamageTypes;
import aiven.zomboidhealthsystem.ModItems;
import aiven.zomboidhealthsystem.ModStatusEffects;
import aiven.zomboidhealthsystem.foundation.player.Health;
import aiven.zomboidhealthsystem.foundation.utility.Util;
import aiven.zomboidhealthsystem.foundation.world.ModServer;
import net.minecraft.block.Blocks;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class Temperature extends Moodle {
    public static final int UPDATE_MULTIPLIER = Math.max(20 / Health.UPDATE_FREQUENCY, 1);
    public static final int UPDATE_FREQUENCY = Health.UPDATE_FREQUENCY * UPDATE_MULTIPLIER;

    public static final float MIN_COMFORTABLE_TEMPERATURE = Config.MIN_COMFORTABLE_TEMP.getValue();
    public static final float MAX_COMFORTABLE_TEMPERATURE = Config.MAX_COMFORTABLE_TEMP.getValue();
    public static final float AVERAGE_TEMPERATURE_BODY = 37.0F;
    public static final float MIN_TEMPERATURE_BODY = AVERAGE_TEMPERATURE_BODY - 6;
    public static final float MAX_TEMPERATURE_BODY = AVERAGE_TEMPERATURE_BODY + 6;

    private final DamageSource hypothermia;
    private final DamageSource hyperthermia;
    private float heat = 0;
    private float heatFromCampfire = 0;
    private float heatFromFurnace = 0;
    private int i = 0;


    public Temperature(Health health) {
        super(health);
        setAmount(AVERAGE_TEMPERATURE_BODY);
        this.hypothermia = Util.getDamageSource(ModDamageTypes.HYPOTHERMIA,getPlayer().getWorld());
        this.hyperthermia = Util.getDamageSource(ModDamageTypes.HYPERTHERMIA,getPlayer().getWorld());
    }

    @Override
    public void update() {
        float deltaTemp = Math.abs(this.getAmount() - AVERAGE_TEMPERATURE_BODY);
        boolean isOverWorld = isOverWorld();
        this.getHealth().getExhaustion().addMultiplier(this, 1.0F);
        if(ModServer.WORLD_SETTINGS.hasTemperature()) {
            if (i++ >= UPDATE_MULTIPLIER - 1) {
                Thirst thirst = getHealth().getThirst();

                float temperature = getPerceivedTemperature();
                float minComfortableTemp = getMinComfortableTemperature();
                float maxComfortableTemp = getMaxComfortableTemperature();

                boolean isDecreasing = false, isIncreasing = false;

                if (temperature < minComfortableTemp && isOverWorld) {
                    this.amount -= (((minComfortableTemp - temperature) / 2)
                            / 10000
                            * (20.0F / Math.max(10.0F, getPlayer().getHungerManager().getFoodLevel()))
                            * Config.TEMPERATURE_MULTIPLIER.getValue()
                            * UPDATE_FREQUENCY
                    );
                    isDecreasing = true;
                } else if (temperature > maxComfortableTemp && isOverWorld) {
                    this.amount += (((temperature - maxComfortableTemp) / 2)
                            / 10000
                            * Math.max(1, thirst.getAmount())
                            * Config.TEMPERATURE_MULTIPLIER.getValue()
                            * UPDATE_FREQUENCY
                    );
                    isIncreasing = true;
                } else {
                    if (this.amount < AVERAGE_TEMPERATURE_BODY - 0.1F) {
                        this.amount += 0.003F
                                * Config.TEMPERATURE_MULTIPLIER.getValue()
                                * UPDATE_FREQUENCY;
                        getHealth().getHunger().reduceHunger(1.0F / 10000F * UPDATE_FREQUENCY);
                    } else if (this.amount > AVERAGE_TEMPERATURE_BODY + 0.1F) {
                        this.amount -= 0.003f
                                / ((thirst.getAmount() + 1.0F) * 1.5F)
                                * Config.TEMPERATURE_MULTIPLIER.getValue()
                                * UPDATE_FREQUENCY;
                        thirst.setAmount(thirst.getAmount() + (thirst.getSpeed() / 3.0F));
                    }
                }

                if(isDecreasing) {
                    getHealth().addStatusEffect(ModStatusEffects.COLD_WEATHER, 0, 15 * 20);
                } else if(getPlayer().hasStatusEffect(ModStatusEffects.COLD_WEATHER)){
                    getHealth().removeStatusEffect(ModStatusEffects.COLD_WEATHER);
                }

                if(isIncreasing) {
                    getHealth().addStatusEffect(ModStatusEffects.HOT_WEATHER, 0, 15 * 20);
                } else if(getPlayer().hasStatusEffect(ModStatusEffects.HOT_WEATHER)){
                    getHealth().removeStatusEffect(ModStatusEffects.HOT_WEATHER);
                }

                if(deltaTemp >= 1.5F) {
                    this.getHealth().getExhaustion().addMultiplier(this, deltaTemp / 1.5F);
                    if(deltaTemp >= 3.0F) {
                        this.getHealth().addStatusEffect(StatusEffects.SLOWNESS, (int) deltaTemp / 2 - 1, 15 * 20);
                        this.getHealth().addStatusEffect(StatusEffects.MINING_FATIGUE, (int) deltaTemp / 2 - 1, 15 * 20);
                        this.getHealth().addStatusEffect(StatusEffects.WEAKNESS, (int) deltaTemp / 2 - 1, 15 * 20);
                        if(random(2 * 60 * 20)) {
                            this.getHealth().addStatusEffect(StatusEffects.NAUSEA, 0, 5);
                        }
                        if(random(2 * 60 * 20)) {
                            this.getHealth().addStatusEffect(StatusEffects.DARKNESS, 0, 5);
                        }
                        if(deltaTemp >= 4.5F) {
                            if(random(2 * 60 * 20)) {
                                getHealth().stumble(0);
                            }
                            if(random(60 * 20)) {
                                this.getHealth().addStatusEffect(StatusEffects.NAUSEA, 0, 5);
                            }
                            if(random(60 * 20)) {
                                this.getHealth().addStatusEffect(StatusEffects.DARKNESS, 0, 5);
                            }
                        }
                    }
                }

                if(this.getAmount() < AVERAGE_TEMPERATURE_BODY - 1.5F) {
                    this.getHealth().addStatusEffect(ModStatusEffects.HYPOTHERMIA, (int) (deltaTemp / 1.5F) - 1, 15 * 20);

                } else if(this.getAmount() > AVERAGE_TEMPERATURE_BODY + 1.5F) {
                    this.getHealth().addStatusEffect(ModStatusEffects.HYPERTHERMIA, (int) (deltaTemp / 1.5F) - 1, 15 * 20);
                }

                if (this.amount < MIN_TEMPERATURE_BODY) {
                    this.getHealth().onDeath(this.hypothermia);
                }
                if (this.amount > MAX_TEMPERATURE_BODY) {
                    this.getHealth().onDeath(this.hyperthermia);
                }

                i = 0;
            }

        }

        heat = 0;
        heatFromCampfire = 0;
        heatFromFurnace = 0;
    }

    @Override
    public void onSleep(){
        if(Math.pow(AVERAGE_TEMPERATURE_BODY - this.getAmount(),1) > 1){
            this.heal(5);
        }
    }

    @Override
    StatusEffect getEffect() {
        return null;
    }

    @Override
    public String getId() {
        return "temperature";
    }

    @Override
    public String getNbt() {
        if(getAmount() == AVERAGE_TEMPERATURE_BODY) {
            return null;
        } else {
            return super.getNbt();
        }
    }

    public float getPerceivedTemperature() {
        return getInternalHeat() + getHeatFromWeather() + getHeatFromHeatSources() + getHeatFromArmor();
    }

    public float getInternalHeat() {
        float temperature = 0;
        temperature -= getHealth().getWet().getAmount() * 2.5F;
        temperature += getHeat();
        return temperature;
    }

    public float getHeatFromWeather() {
        float temperature = ModServer.WEATHER.getTemperatureAtPos(getPlayer().getBlockPos());

        if (getPlayer().hasStatusEffect(ModStatusEffects.WIND)) {
            int amplifier = getPlayer().getStatusEffect(ModStatusEffects.WIND).getAmplifier();
            temperature -= amplifier * (getHealth().getWet().getAmount() / 2 + 1);
        }

        return temperature;
    }

    public float getHeatFromHeatSources() {
        return getHeatFromCampfire() + getHeatFromFurnace() + getHeatFromLava();
    }

    private float getMinComfortableTemperature() {
        Health health = getHealth();
        Thirst thirst = health.getThirst();
        float hpPercent = (float) Math.sqrt(health.getBodyHpPercent());
        float foodLvlPercent = (float) getPlayer().getHungerManager().getFoodLevel() / 20;
        foodLvlPercent = (float) (Math.cbrt(Math.cbrt(foodLvlPercent)));
        return MIN_COMFORTABLE_TEMPERATURE / hpPercent / foodLvlPercent + ((Math.max(thirst.getAmount() - 1,0)) * 2);
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
        this.heat += heat;
    }

    public float getHeat() {
        return heat;
    }

    public float getHeatFromLava() {
        float distance = Util.getDistance(getPlayer().getWorld(), Blocks.LAVA, getPlayer().getBlockPos(), 5);
        if(distance == -1 || distance >= 5) {
            return 0;
        }
        return 25.0F - (distance * 5);
    }

    private float getHeatFromArmor(){
        PlayerEntity player = this.getHealth().getPlayer();
        float heat = 0;
        for(ItemStack stack : player.getArmorItems()){
            Item item = stack.getItem();
            if(!item.equals(Items.AIR)){
                if(item.equals(Items.LEATHER_HELMET)){
                    heat += Config.HEAT_FROM_LEATHER_HELMET.getValue();
                } else if(item.equals(Items.LEATHER_CHESTPLATE)){
                    heat += Config.HEAT_FROM_LEATHER_CHESTPLATE.getValue();
                } else if(item.equals(Items.LEATHER_LEGGINGS)){
                    heat += Config.HEAT_FROM_LEATHER_LEGGINGS.getValue();
                } else if(item.equals(Items.LEATHER_BOOTS)){
                    heat += Config.HEAT_FROM_LEATHER_BOOTS.getValue();
                } else if(item.equals(ModItems.WOOL_HELMET)){
                    heat += Config.HEAT_FROM_WOOL_HELMET.getValue();
                } else if(item.equals(ModItems.WOOL_CHESTPLATE)){
                    heat += Config.HEAT_FROM_WOOL_CHESTPLATE.getValue();
                } else if(item.equals(ModItems.WOOL_LEGGINGS)){
                    heat += Config.HEAT_FROM_WOOL_LEGGINGS.getValue();
                } else if(item.equals(ModItems.WOOL_BOOTS)){
                    heat += Config.HEAT_FROM_WOOL_BOOTS.getValue();
                } else {
                    heat += Config.HEAT_FROM_OTHER_ARMOR.getValue();
                }
            }
        }
        return heat;
    }
}