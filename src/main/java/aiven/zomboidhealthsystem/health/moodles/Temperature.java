package aiven.zomboidhealthsystem.health.moodles;

import aiven.zomboidhealthsystem.*;
import aiven.zomboidhealthsystem.health.Health;
import aiven.zomboidhealthsystem.utility.Util;

import de.marhali.json5.Json5;
import de.marhali.json5.Json5Object;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.io.IOException;


public final class Temperature extends ImportantMoodle {
    private static final Json5 mapper = new Json5();

    public static final float MIN_COMFORTABLE_TEMPERATURE = Config.getInstance().min_comfortable_temp;
    public static final float MAX_COMFORTABLE_TEMPERATURE = Config.getInstance().max_comfortable_temp;

    public static final float AVERAGE_TEMPERATURE_BODY = 37.0F;

    public static final float MIN_TEMPERATURE_BODY = AVERAGE_TEMPERATURE_BODY - 6;
    public static final float MAX_TEMPERATURE_BODY = AVERAGE_TEMPERATURE_BODY + 6;

    private boolean isFeelingCold = false;
    private boolean isFeelingHot = false;

    private float heat = 0;

    private float heatFromBlocks = -1;
    private float heatFromBlocksTickDelta = 0;


    public Temperature(Health health) {
        super(health);
        setValue(AVERAGE_TEMPERATURE_BODY);
    }

    @Override
    public String getId() {
        return "temperature";
    }

    @Override
    public boolean canPlayerLive() {
        return getValue() != minValue() && getValue() != maxValue();
    }

    @Override
    public float minValue() {
        return MIN_TEMPERATURE_BODY;
    }

    @Override
    public float maxValue() {
        return MAX_TEMPERATURE_BODY;
    }

    @Override
    protected float defaultValue() {
        return AVERAGE_TEMPERATURE_BODY;
    }

    @Override
    public void _update(float tickDelta) {
        if(tickDelta != 0) {
            float deltaTemp = Math.abs(this.getValue() - AVERAGE_TEMPERATURE_BODY);
            boolean isOverWorld = isOverWorld();
            if (Config.getInstance().temperature) {
                if (heatFromBlocks == -1) {
                    heatFromBlocks = getHeatFromHeatSources();
                } else {
                    heatFromBlocksTickDelta += tickDelta;
                    if (heatFromBlocksTickDelta >= 20) {
                        heatFromBlocksTickDelta = 0;
                        heatFromBlocks = getHeatFromHeatSources();
                    }
                }

                Thirst thirst = getHealth().getThirst();
                Hunger hunger = getHealth().getHunger();

                thirst.setMultiplier(this, 1.0F);
                hunger.setMultiplier(this, 1.0F);
                this.getHealth().getExhaustion().setMultiplier(this, 1.0F);

                float temperature = getPerceivedTemperature();
                float minComfortableTemp = getMinComfortableTemperature();
                float maxComfortableTemp = getMaxComfortableTemperature();

                if (temperature < minComfortableTemp && isOverWorld) {
                    this.value -= (((minComfortableTemp - temperature) / 2)
                            / 10000
                            * Math.max(1, (hunger.getValue() / 2.5F) + 1)
                            * Config.getInstance().temperature_multiplier
                            * tickDelta
                    );
                    isFeelingCold = true;
                    isFeelingHot = false;
                } else if (temperature > maxComfortableTemp && isOverWorld) {
                    this.value += (((temperature - maxComfortableTemp) / 2)
                            / 10000
                            * Math.max(1, thirst.getValue())
                            * Config.getInstance().temperature_multiplier
                            * tickDelta
                    );
                    isFeelingHot = true;
                    isFeelingCold = false;
                } else {
                    isFeelingHot = false;
                    isFeelingCold = false;
                    if (this.value < AVERAGE_TEMPERATURE_BODY - 0.1F) {
                        this.value += 0.003F
                                * Config.getInstance().temperature_multiplier
                                * tickDelta;
                        hunger.setMultiplier(this, 1.33F);
                    } else if (this.value > AVERAGE_TEMPERATURE_BODY + 0.1F) {
                        this.value -= 0.003F
                                / ((thirst.getValue() + 1.0F) * 1.5F)
                                * Config.getInstance().temperature_multiplier
                                * tickDelta;
                        thirst.setMultiplier(this, 1.33F);
                    }
                }

                if (deltaTemp >= 1.5F) {
                    this.getHealth().getExhaustion().setMultiplier(this, deltaTemp / 1.5F);
                    if (deltaTemp >= 3.0F) {
                        this.getHealth().addMobEffect(MobEffects.MOVEMENT_SLOWDOWN, (int) deltaTemp / 2 - 1, 15 * 20);
                        this.getHealth().addMobEffect(MobEffects.DIG_SLOWDOWN, (int) deltaTemp / 2 - 1, 15 * 20);
                        this.getHealth().addMobEffect(MobEffects.WEAKNESS, (int) deltaTemp / 2 - 1, 15 * 20);
                        if (Util.random(1.0F / (2 * 60 * 20))) {
                            this.getHealth().addMobEffect(MobEffects.CONFUSION, 0, 10 * 20);
                        }
                        if (Util.random(1.0F / (2 * 60 * 20))) {
                            this.getHealth().addMobEffect(MobEffects.DARKNESS, 0, 5 * 20);
                        }
                        if (deltaTemp >= 4.5F) {
                            if (Util.random(1.0F / (60 * 20))) {
                                this.getHealth().addMobEffect(MobEffects.CONFUSION, 0, 10 * 20);
                            }
                            if (Util.random(1.0F / (60 * 20))) {
                                this.getHealth().addMobEffect(MobEffects.DARKNESS, 0, 5 * 20);
                            }
                        }
                    }
                }
            }
            heat = 0;
        }
    }

    @Override
    protected void die() {
        if (this.value <= MIN_TEMPERATURE_BODY) {
            this.getHealth().die(Util.getDamageSource(ModDamageTypes.HYPOTHERMIA, getPlayer().level()));
        }
        if (this.value >= MAX_TEMPERATURE_BODY) {
            this.getHealth().die(Util.getDamageSource(ModDamageTypes.HYPERTHERMIA, getPlayer().level()));
        }
    }

    @Override
    public void readNbt(String value) {
        if(value != null) {
            Json5Object object = mapper.parse(value).getAsJson5Object();
            setValue(object.get("value").getAsFloat());
            isFeelingCold = object.get("cold").getAsBoolean();
            isFeelingHot = object.get("hot").getAsBoolean();
        } else {
            setValue(defaultValue());
            isFeelingHot = false;
            isFeelingCold = false;
        }
    }

    @Override
    protected ResourceKey<DamageType> getDamageType() {
        return null;
    }

    @Override
    public String getNbt() {
        Json5Object object = new Json5Object();
        object.addProperty("value", getValue());
        object.addProperty("cold", isFeelingCold);
        object.addProperty("hot", isFeelingHot);
        try {
            return mapper.serialize(object);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResourceLocation getMoodleIconTexture() {
        if(this.getValue() > AVERAGE_TEMPERATURE_BODY + 1.5F) {
            return ZomboidHealthSystem.getResourceLocation("textures/moodle/moodle_icon_hyperthermia.png");
        } else if(this.getValue() < AVERAGE_TEMPERATURE_BODY - 1.5F) {
            return ZomboidHealthSystem.getResourceLocation("textures/moodle/moodle_icon_hypothermia.png");
        } else {
            return null;
        }
    }

    @Override
    public String getMoodleIconText() {
        if (this.getValue() > AVERAGE_TEMPERATURE_BODY + 1.5F) {
            return Component.translatable(ZomboidHealthSystem.ID + ".moodle.hyperthermia").getString() + " " + getAmplifier();
        } else if (this.getValue() < AVERAGE_TEMPERATURE_BODY - 1.5F) {
            return Component.translatable(ZomboidHealthSystem.ID + ".moodle.hypothermia").getString() + " " + getAmplifier();
        } else {
            return null;
        }
    }

    @Override
    public int getAmplifier() {
        return (int) (Math.abs(AVERAGE_TEMPERATURE_BODY - getValue()) / 1.5F);
    }

    @Override
    public void sleep(int sumTicks) {

    }

    public float getPerceivedTemperature() {
        return getTemperatureAtUserPos() + getHeat() + heatFromBlocks + getHeatFromArmor();
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
        float distance = Util.getMinDistance(getLevel(), getPlayer().blockPosition(), 5, blockState -> {
            for(Block block : new Block[]{
                    Blocks.LAVA,
                    Blocks.CAMPFIRE,
                    Blocks.SOUL_CAMPFIRE,
                    Blocks.FIRE,
                    Blocks.SOUL_FIRE
            }) {
                if(blockState.getBlock().equals(block)) {
                    return true;
                }
            }
            if(blockState.getBlock() instanceof AbstractFurnaceBlock furnace) {
                if(blockState.getValue(AbstractFurnaceBlock.LIT)) {
                    return true;
                }
            }
            return false;
        });
        if(distance == -1 || distance >= 5) {
            return 0;
        }
        return Config.getInstance().max_heat_from_block - (distance * Config.getInstance().max_heat_from_block / 5);
    }

    private float getMinComfortableTemperature() {
        Health health = getHealth();
        Thirst thirst = health.getThirst();
        float hpPercent = (float) Math.sqrt(health.getBodyHpPercent());
        float foodLvl = Math.max(getHealth().getHunger().getValue(), 1);
        foodLvl = (float) (Math.cbrt(Math.cbrt(foodLvl)));
        return MIN_COMFORTABLE_TEMPERATURE / hpPercent * foodLvl + ((Math.max(thirst.getValue() - 1,0)) * 2);
    }

    private float getMaxComfortableTemperature() {
        Health health = getHealth();
        Thirst thirst = health.getThirst();
        return MAX_COMFORTABLE_TEMPERATURE - ((Math.max(thirst.getValue() - 1,0)) * 2);
    }

    public void heal(int divisor){
        setTemperature(((AVERAGE_TEMPERATURE_BODY * divisor) + this.getValue()) / (2 + (divisor - 1)));
    }

    public void cool(float amount){
        if(this.getValue() - amount > AVERAGE_TEMPERATURE_BODY) {
            this.value -= amount;
        } else {
            this.value -= amount / 2;
        }
    }

    public void setHeat(float heat) {
        this.heat = heat;
    }

    public void setTemperature(float temperature) {
        this.value = temperature;
    }

    public void addHeat(float heat) {
        this.setHeat(getHeat() + heat);
    }

    public float getHeat() {
        return heat;
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