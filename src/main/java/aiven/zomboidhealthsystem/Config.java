package aiven.zomboidhealthsystem;

import aiven.zomboidhealthsystem.gui.BodyWidgetType;

import com.github.HawkeyeAiven.json5.ObjectMapper;
import com.github.HawkeyeAiven.json5.annotations.Comment;

import de.marhali.json5.Json5;

import net.minecraft.client.Minecraft;

import org.slf4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public final class Config {
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

    public Boolean temperature = true;
    public Boolean pain_keeps_awake = true;
    public Boolean time_hud = true;
    public Boolean health_hud = true;
    public Boolean moodles_hud = true;
    public Boolean show_injured_icon = true;

    @Comment("Speed multipliers")
    public Float temperature_multiplier = 1.0F;
    public Float drowsiness_multiplier = 1.0F;
    public Float thirst_multiplier = 1.0F;
    public Float exhaustion_multiplier = 1.0F;
    public Float wet_multiplier = 1.0F;
    public Float hunger_multiplier = 1.0F;
    public Float damage_multiplier = 1.0F;
    public Float sleeping_pills_multiplier = 1.0F;

    public Float wind_chance = 0.33F;
    public Float infection_chance = 1.0F / (1.5F * 60 * 20);
    public Float cold_chance = 1.0F / (9 * 60 * 20);
    public Float average_bleeding_chance = 0.25F;
    public Float zombification_chance = 0.25F;
    public Float exhaustion_per_jump = 0.020F;

    public Float min_comfortable_temp = 10.0F;
    public Float max_comfortable_temp = 30.0F;

    public Float heat_from_wool_helmet = 6.5F;
    public Float heat_from_wool_chestplate = 8.5F;
    public Float heat_from_wool_leggings = 7.5F;
    public Float heat_from_wool_boots = 6.5F;

    public Float heat_from_leather_helmet = 4.5F;
    public Float heat_from_leather_chestplate = 6.5F;
    public Float heat_from_leather_leggings = 5.0F;
    public Float heat_from_leather_boots = 4.5F;

    public Float heat_from_other_armor = 1.0F;

    public Float max_heat_from_block = 30.0F;

    public Integer heal_time = 40 * 60 * 20;
    public Integer food_heal_amount = 2;
    public Integer update_frequency = 1;
    public Integer min_drowsiness_for_sleep = 1;
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
        if(file.exists()) {
            try {
                try (FileReader reader = new FileReader(file)) {
                    config = mapper.parse(reader, Config.class);
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
                mapper.serialize(config, writer);
            }
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    public static Config getInstance() {
        return config;
    }
}
