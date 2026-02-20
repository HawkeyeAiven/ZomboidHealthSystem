package aiven.zomboidhealthsystem.foundation.player.moodles;

import aiven.zomboidhealthsystem.*;
import aiven.zomboidhealthsystem.foundation.player.Health;
import aiven.zomboidhealthsystem.foundation.utility.Util;
import aiven.zomboidhealthsystem.foundation.world.ModServer;
import aiven.zomboidhealthsystem.infrastructure.config.Json;
import aiven.zomboidhealthsystem.infrastructure.config.JsonBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class Temperature extends Moodle {
    public static final int UPDATE_MULTIPLIER = Math.max(20 / Health.UPDATE_FREQUENCY, 1);
    public static final int UPDATE_FREQUENCY = Health.UPDATE_FREQUENCY * UPDATE_MULTIPLIER;

    public static final float MIN_COMFORTABLE_TEMPERATURE = Config.MIN_COMFORTABLE_TEMP.getValue();
    public static final float MAX_COMFORTABLE_TEMPERATURE = Config.MAX_COMFORTABLE_TEMP.getValue();
    public static final float AVERAGE_TEMPERATURE_BODY = 37.0F;
    public static final float MIN_TEMPERATURE_BODY = AVERAGE_TEMPERATURE_BODY - 6;
    public static final float MAX_TEMPERATURE_BODY = AVERAGE_TEMPERATURE_BODY + 6;

    private boolean isFeelingCold = false;
    private boolean isFeelingHot = false;

    private float heat = 0;
    private float heatFromCampfire = 0;
    private float heatFromFurnace = 0;
    private int i = 0;


    public Temperature(Health health) {
        super(health);
        setAmount(AVERAGE_TEMPERATURE_BODY);
    }

    @Override
    public void update() {
        float deltaTemp = Math.abs(this.getAmount() - AVERAGE_TEMPERATURE_BODY);
        boolean isOverWorld = isOverWorld();
        this.getHealth().getExhaustion().addMultiplier(this, 1.0F);
        if(ModServer.WORLD_SETTINGS.hasTemperature()) {
            if (i++ >= UPDATE_MULTIPLIER - 1) {
                Thirst thirst = getHealth().getThirst();
                Hunger hunger = getHealth().getHunger();

                thirst.addMultiplier(this, 1.0F);
                hunger.addMultiplier(this, 1.0F);

                float temperature = getPerceivedTemperature();
                float minComfortableTemp = getMinComfortableTemperature();
                float maxComfortableTemp = getMaxComfortableTemperature();

                if (temperature < minComfortableTemp && isOverWorld) {
                    this.amount -= (((minComfortableTemp - temperature) / 2)
                            / 10000
                            * Math.max(1, (hunger.getAmount() / 2.5F) + 1)
                            * Config.TEMPERATURE_MULTIPLIER.getValue()
                            * UPDATE_FREQUENCY
                    );
                    isFeelingCold = true;
                    isFeelingHot = false;
                } else if (temperature > maxComfortableTemp && isOverWorld) {
                    this.amount += (((temperature - maxComfortableTemp) / 2)
                            / 10000
                            * Math.max(1, thirst.getAmount())
                            * Config.TEMPERATURE_MULTIPLIER.getValue()
                            * UPDATE_FREQUENCY
                    );
                    isFeelingHot = true;
                    isFeelingCold = false;
                } else {
                    isFeelingHot = false;
                    isFeelingCold = false;
                    if (this.amount < AVERAGE_TEMPERATURE_BODY - 0.1F) {
                        this.amount += 0.003F
                                * Config.TEMPERATURE_MULTIPLIER.getValue()
                                * UPDATE_FREQUENCY;
                        hunger.addMultiplier(this, 1.33F);
                    } else if (this.amount > AVERAGE_TEMPERATURE_BODY + 0.1F) {
                        this.amount -= 0.003F
                                / ((thirst.getAmount() + 1.0F) * 1.5F)
                                * Config.TEMPERATURE_MULTIPLIER.getValue()
                                * UPDATE_FREQUENCY;
                        thirst.addMultiplier(this, 1.33F);
                    }
                }

                if(deltaTemp >= 1.5F) {
                    this.getHealth().getExhaustion().addMultiplier(this, deltaTemp / 1.5F);
                    if(deltaTemp >= 3.0F) {
                        this.getHealth().addStatusEffect(StatusEffects.SLOWNESS, (int) deltaTemp / 2 - 1, 15 * 20);
                        this.getHealth().addStatusEffect(StatusEffects.MINING_FATIGUE, (int) deltaTemp / 2 - 1, 15 * 20);
                        this.getHealth().addStatusEffect(StatusEffects.WEAKNESS, (int) deltaTemp / 2 - 1, 15 * 20);
                        if(once(2 * 60 * 20)) {
                            this.getHealth().addStatusEffect(StatusEffects.NAUSEA, 0, 5);
                        }
                        if(once(2 * 60 * 20)) {
                            this.getHealth().addStatusEffect(StatusEffects.DARKNESS, 0, 5);
                        }
                        if(deltaTemp >= 4.5F) {
                            if(once(2 * 60 * 20)) {
                                getHealth().stumble(0);
                            }
                            if(once(60 * 20)) {
                                this.getHealth().addStatusEffect(StatusEffects.NAUSEA, 0, 5);
                            }
                            if(once(60 * 20)) {
                                this.getHealth().addStatusEffect(StatusEffects.DARKNESS, 0, 5);
                            }
                        }
                    }
                }

                if (this.amount < MIN_TEMPERATURE_BODY) {
                    this.getHealth().onDeath(Util.getDamageSource(ModDamageTypes.HYPOTHERMIA,getPlayer().getWorld()));
                }
                if (this.amount > MAX_TEMPERATURE_BODY) {
                    this.getHealth().onDeath(Util.getDamageSource(ModDamageTypes.HYPERTHERMIA,getPlayer().getWorld()));
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
    public Identifier getMoodleIconTexture() {
        if(this.getAmount() > AVERAGE_TEMPERATURE_BODY + 1.5F) {
            return Identifier.of(ZomboidHealthSystem.ID, "textures/moodle/moodle_icon_hyperthermia.png");
        } else if(this.getAmount() < AVERAGE_TEMPERATURE_BODY - 1.5F) {
            return Identifier.of(ZomboidHealthSystem.ID, "textures/moodle/moodle_icon_hypothermia.png");
        } else {
            return null;
        }
    }

    @Override
    public String getMoodleIconText() {
        if(this.getAmount() > AVERAGE_TEMPERATURE_BODY + 1.5F) {
            return Text.translatable("zomboidhealthsystem.text.hyperthermia").getString();
        } else if(this.getAmount() < AVERAGE_TEMPERATURE_BODY - 1.5F) {
            return Text.translatable("zomboidhealthsystem.text.hypothermia").getString();
        } else {
            return null;
        }
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
            JsonBuilder builder = new JsonBuilder();
            builder.append("amount", String.valueOf(this.getAmount()));
            if(this.isFeelingHot()) {
                builder.append("feeling_hot", "true");
            }
            if(this.isFeelingCold()) {
                builder.append("feeling_cold", "true");
            }
            return builder.toString();
        }
    }

    @Override
    public void readNbt(String value) {
        String amount = Json.getValue(value, "amount");
        if(amount != null) {
            this.setAmount(Float.parseFloat(amount));
        } else {
            this.setAmount(AVERAGE_TEMPERATURE_BODY);
        }
        String feeling_hot = Json.getValue(value, "feeling_hot");
        if(feeling_hot != null) {
            this.isFeelingHot = Boolean.parseBoolean(feeling_hot);
        } else {
            this.isFeelingHot = false;
        }
        String feeling_cold = Json.getValue(value, "feeling_cold");
        if(feeling_cold != null) {
            this.isFeelingCold = Boolean.parseBoolean(feeling_cold);
        } else {
            this.isFeelingCold = false;
        }
    }

    @Override
    public int getAmplifier() {
        return (int) (Math.abs(AVERAGE_TEMPERATURE_BODY - getAmount()) / 1.5F);
    }

    public float getPerceivedTemperature() {
        return getTemperatureAtPos() + getHeat() + getHeatFromHeatSources() + getHeatFromArmor();
    }

    public float getTemperatureAtPos() {
        return ModServer.WEATHER.getTemperatureAtPos(getPlayer().getBlockPos());
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
        float distance = Util.getDistance(getPlayer().getWorld(), Blocks.LAVA, getPlayer().getBlockPos(), 5);
        if(distance == -1 || distance >= 5) {
            return 0;
        }
        return Config.MAX_HEAT_FROM_BLOCK.getValue() - (distance * Config.MAX_HEAT_FROM_BLOCK.getValue() / 5);
    }

    public boolean isFeelingCold() {
        return isFeelingCold;
    }

    public boolean isFeelingHot() {
        return isFeelingHot;
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