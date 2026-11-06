package aiven.zomboidhealthsystem;

import aiven.zomboidhealthsystem.gui.BodyWidgetType;
import aiven.zomboidhealthsystem.utility.Util;

import com.github.HawkeyeAiven.json5.ObjectMapper;
import com.github.HawkeyeAiven.json5.annotations.Comment;

import de.marhali.json5.Json5;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.Items;

import org.slf4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;

public final class Config {
    //TODO Можно получать через API
    // Forge: FMLPaths.CONFIGDIR.get()
    // Fabric: FabricLoader.getInstance().getConfigDir()
    private static final File configDirectory = new File(Minecraft.getInstance().gameDirectory, "config");
    private static final File file = new File(configDirectory, ZomboidHealthSystem.ID_VER + ".json5");

    private static final Json5 json5 = Json5.builder(builder -> builder
            .writeComments()
            .prettyPrinting()
            .build()
    );

    private static final ObjectMapper mapper = new ObjectMapper(json5);
    private static final Logger log = ZomboidHealthSystem.log;

    private static Config config;

    public Boolean temperature_enabled = true;
    @Comment("The rate at which body temperature increases or decreases.")
    public Float body_temperature_speed_multiplier = 1.0F;
    public Float min_comfortable_temp = 10.0F;
    public Float max_comfortable_temp = 30.0F;
    public Float max_heat_from_block = 30.0F;
    public LinkedHashMap<String, Float> heat_values = new LinkedHashMap<>();
    {
        heat_values.put(Util.getStringId(ModItems.WOOL_HELMET), 6.5F);
        heat_values.put(Util.getStringId(ModItems.WOOL_CHESTPLATE), 8.5F);
        heat_values.put(Util.getStringId(ModItems.WOOL_LEGGINGS), 7.5F);
        heat_values.put(Util.getStringId(ModItems.WOOL_BOOTS), 6.5F);
        heat_values.put(Util.getStringId(Items.LEATHER_HELMET), 4.5F);
        heat_values.put(Util.getStringId(Items.LEATHER_CHESTPLATE), 6.5F);
        heat_values.put(Util.getStringId(Items.LEATHER_LEGGINGS), 5.0F);
        heat_values.put(Util.getStringId(Items.LEATHER_BOOTS), 4.5F);
    }
    public Float heat_from_other_armor = 1.0F;

    @Comment("The rate at which drowsiness increases. A value of \"0\" disables it.")
    public Float drowsiness_speed_multiplier = 1.0F;
    public Integer min_drowsiness_for_sleep = 1;
    public Float sleeping_pills_speed_multiplier = 1.0F;

    @Comment("Pain prevents you from falling asleep")
    public Boolean pain_keeps_awake = true;

    @Comment("The rate at which thirst increases. A value of \"0\" disables it.")
    public Float thirst_speed_multiplier = 1.0F;
    public LinkedHashMap<String, Float> thirst_values = new LinkedHashMap<>();
    {
        thirst_values.put(Util.getStringId(Items.APPLE), 0.2F);
        thirst_values.put(Util.getStringId(Items.MUSHROOM_STEW), 0.4F);
        thirst_values.put(Util.getStringId(Items.MELON_SLICE), 0.4F);
        thirst_values.put(Util.getStringId(Items.BEETROOT_SOUP), 0.4F);
        thirst_values.put(Util.getStringId(Items.SWEET_BERRIES), 0.05F);
        thirst_values.put(Util.getStringId(Items.GLOW_BERRIES), 0.05F);
        thirst_values.put(Util.getStringId(Items.RABBIT_STEW), 0.35F);
        thirst_values.put(Util.getStringId(Items.MILK_BUCKET), 2.0F);
        thirst_values.put(Util.getStringId(ModItems.BOTTLE_OF_PURIFIED_WATER), 1.25F);
        thirst_values.put(Util.getStringId(ModItems.CUP_OF_COFFEE), 0.75F);
        thirst_values.put(Util.getStringId(ModItems.BOTTLE_OF_WINE), 1.25F);
    }

    @Comment("The rate at which you become tired or recover. A value of \"0\" disables it.")
    public Float exhaustion_speed_multiplier = 1.0F;
    public Float exhaustion_per_jump = 0.020F;

    @Comment("The rate at which wetness increases. A value of \"0\" disables it.")
    public Float wetness_speed_multiplier = 1.0F;

    @Comment("The rate at which hunger increases. A value of \"0\" disables it.")
    public Float hunger_speed_multiplier = 1.0F;
    public Integer food_heal_amount = 2;

    public Float damage_multiplier = 1.0F;

    public Float common_cold_chance = 1.0F / (9 * 60 * 20);

    public Float wind_update_chance = 1.0F / 12000.0F;
    public Float wind_chance = 0.33F;
    public Float min_wind = 3.0F;
    public Float max_wind = 15.0F;

    public Float infection_chance = 1.0F / (1.5F * 60 * 20);
    public Float average_bleeding_chance = 0.25F;
    public Float heal_value = 1.0F / (40 * 60 * 20);

    public Float zombification_chance = 0.25F;

    public Integer update_frequency = 1;

    public Boolean time_hud = true;
    public Boolean health_hud = true;
    public Boolean moodles_hud = true;
    public Boolean show_injured_icon = true;

    public Integer moodle_icon_size = 16;
    public Integer moodles_hud_ypos = 90;

    public Integer health_hud_xpos = 10;
    public Integer health_hud_ypos = 30;

    public Integer time_hud_xpos = 6;
    public Integer time_hud_ypos = 6;

    @Comment("""
            "ZOMBOID": Shows damage, bleeding and a bandage
            "SIMPLE": Shows the level of damage and the presence of a bandage\
            """)
    public String body_widget_type = BodyWidgetType.ZOMBOID.toString();

    public static void initialize() {
        if (file.exists()) {
            try {
                try (FileReader reader = new FileReader(file)) {
                    config = mapper.read(reader, Config.class);
                }
            } catch (Exception e) {
                log.error("Failed to load config", e);
                config = new Config();
            }
        } else {
            file.getParentFile().mkdirs();
            config = new Config();
        }
        try {
            save(config);
        } catch (Exception e) {
            log.error("Failed to save config", e);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                save(config);
            } catch (IOException e) {
                log.error("Failed to save config", e);
            }
        }));
    }

    private static void save(Config config) throws IOException {
        try {
            try (FileWriter writer = new FileWriter(file)) {
                mapper.write(config, writer);
            }
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    public static Config getInstance() {
        return config;
    }
}