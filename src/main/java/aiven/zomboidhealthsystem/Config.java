package aiven.zomboidhealthsystem;


import aiven.zomboidhealthsystem.foundation.world.WorldSettings;
import aiven.zomboidhealthsystem.infrastructure.config.JsonConfig;
import aiven.zomboidhealthsystem.infrastructure.config.codecs.*;
import org.joml.Vector2f;

import java.io.File;
import java.io.IOException;


public class Config {
    private static final JsonConfig CONFIG = new JsonConfig();
    private static final File FILE = new File(System.getProperty("user.dir") + "\\config", ZomboidHealthSystem.ID_VER + ".json");

    public static final BooleanCodec PAIN_KEEPS_AWAKE = (BooleanCodec) CONFIG.add(new BooleanCodec("pain_keeps_awake", true));
    public static final BooleanCodec DELAY_BEFORE_CRAWLING = (BooleanCodec) CONFIG.add(new BooleanCodec("delay_before_crawling", true));
    public static final BooleanCodec TIME_HUD = (BooleanCodec) CONFIG.add(new BooleanCodec("time_hud", true));
    public static final BooleanCodec HEALTH_HUD = (BooleanCodec) CONFIG.add(new BooleanCodec("health_hud", true));
    public static final BooleanCodec TEMPERATURE_DEPENDS_ON_Z_COORDINATE = (BooleanCodec) CONFIG.add(new BooleanCodec("temperature_depends_on_z_coordinate", true));
    public static final BooleanCodec FROZEN_OCEANS_IN_WINTER = (BooleanCodec) CONFIG.add(new BooleanCodec("frozen_ocean_in_winter", true));

    public static final FloatCodec TEMPERATURE_MULTIPLIER = (FloatCodec) CONFIG.add(new FloatCodec("temperature_multiplier", 1.0F));
    public static final FloatCodec COLD_MULTIPLIER = (FloatCodec) CONFIG.add(new FloatCodec("cold_multiplier", 1.0F));
    public static final FloatCodec DROWSINESS_MULTIPLIER = (FloatCodec) CONFIG.add(new FloatCodec("drowsiness_multiplier", 1.0F));
    public static final FloatCodec THIRST_MULTIPLIER = (FloatCodec) CONFIG.add(new FloatCodec("thirst_multiplier", 1.0F));
    public static final FloatCodec EXHAUSTION_MULTIPLIER = (FloatCodec) CONFIG.add(new FloatCodec("exhaustion_multiplier", 1.0F));
    public static final FloatCodec WET_MULTIPLIER = (FloatCodec) CONFIG.add(new FloatCodec("wet_multiplier", 1.0F));
    public static final FloatCodec DAMAGE_MULTIPLIER = (FloatCodec) CONFIG.add(new FloatCodec("damage_multiplier", 1.1F));

    public static final FloatCodec MAX_SEASON_TEMPERATURE = (FloatCodec) CONFIG.add(new FloatCodec("max_season_temperature", 20.0F));
    public static final FloatCodec WIND_CHANCE = (FloatCodec) CONFIG.add(new FloatCodec("wind_chance", 0.33F));
    public static final FloatCodec SLEEPING_PILLS_SPEED = (FloatCodec) CONFIG.add(new FloatCodec("sleeping_pills_speed", 0.0005F));

    public static final FloatCodec COOLING_WORLD_MULTIPLIER = (FloatCodec) CONFIG.add(new FloatCodec("cooling_world_multiplier", 1.1F));
    public static final FloatCodec HEATING_WORLD_MULTIPLIER = (FloatCodec) CONFIG.add(new FloatCodec("heating_world_multiplier", 0.9F));

    public static final FloatCodec MIN_COMFORTABLE_TEMP = (FloatCodec) CONFIG.add(new FloatCodec("min_comfortable_temp", 15.0F));
    public static final FloatCodec MAX_COMFORTABLE_TEMP = (FloatCodec) CONFIG.add(new FloatCodec("max_comfortable_temp", 30.0F));

    public static final FloatCodec HEAT_FROM_WOOL_HELMET = (FloatCodec) CONFIG.add(new FloatCodec("heat_from_wool_helmet", 6.5F));
    public static final FloatCodec HEAT_FROM_WOOL_CHESTPLATE = (FloatCodec) CONFIG.add(new FloatCodec("heat_from_wool_chestplate", 8.5F));
    public static final FloatCodec HEAT_FROM_WOOL_LEGGINGS = (FloatCodec) CONFIG.add(new FloatCodec("heat_from_wool_leggings", 7.5F));
    public static final FloatCodec HEAT_FROM_WOOL_BOOTS = (FloatCodec) CONFIG.add(new FloatCodec("heat_from_wool_boots", 6.5F));

    public static final FloatCodec HEAT_FROM_LEATHER_HELMET = (FloatCodec) CONFIG.add(new FloatCodec("heat_from_leather_helmet", 4.5F));
    public static final FloatCodec HEAT_FROM_LEATHER_CHESTPLATE = (FloatCodec) CONFIG.add(new FloatCodec("heat_from_leather_chestplate", 6.5F));
    public static final FloatCodec HEAT_FROM_LEATHER_LEGGINGS = (FloatCodec) CONFIG.add(new FloatCodec("heat_from_leather_leggings", 5.0F));
    public static final FloatCodec HEAT_FROM_LEATHER_BOOTS = (FloatCodec) CONFIG.add(new FloatCodec("heat_from_leather_boots", 4.5F));

    public static final FloatCodec HEAT_FROM_OTHER_ARMOR = (FloatCodec) CONFIG.add(new FloatCodec("heat_from_other_armor", 1.0F));

    public static final IntegerCodec MIN_HUNGER_FOR_SLEEP = (IntegerCodec) CONFIG.add(new IntegerCodec("min_hunger_for_sleep", 11));
    public static final IntegerCodec HEAL_TIME = (IntegerCodec) CONFIG.add(new IntegerCodec("heal_time", 40 * 60 * 20));
    public static final IntegerCodec FOOD_HEAL_AMOUNT = (IntegerCodec) CONFIG.add(new IntegerCodec("food_heal_amount",2));
    public static final IntegerCodec UPDATE_FREQUENCY = (IntegerCodec) CONFIG.add(new IntegerCodec("update_frequency", 5));
    public static final IntegerCodec MIN_DROWSINESS_FOR_SLEEP = (IntegerCodec) CONFIG.add(new IntegerCodec("min_drowsiness_for_sleep",1));
    public static final IntegerCodec INFECTION_TIME = (IntegerCodec) CONFIG.add(new IntegerCodec("infection_time", 5 * 60 * 20));
    public static final IntegerCodec AUTOSAVE_FREQUENCY = (IntegerCodec) CONFIG.add(new IntegerCodec("autosave_frequency", 60 * 20));

    public static final Vector2fCodec HEALTH_HUD_POS = (Vector2fCodec) CONFIG.add(new Vector2fCodec("health_hud_pos", new Vector2f(10, 30)));
    public static final Vector2fCodec TIME_HUD_POS = (Vector2fCodec) CONFIG.add(new Vector2fCodec("time_hud_pos", new Vector2f(6, 6)));

    public static final WorldSettingsCodec DEFAULT_WORLD_SETTINGS = (WorldSettingsCodec) CONFIG.add(new WorldSettingsCodec("default_world_settings", new WorldSettings(3, 28, 10 * 24000, true, true)));

    public static void initialize() throws IOException {
        if(FILE.exists()) {
            CONFIG.loadValues(FILE);
        } else {
            File configFolder = new File(FILE.getParent());
            configFolder.mkdirs();
            FILE.createNewFile();
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                CONFIG.save(FILE);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }));
    }
}