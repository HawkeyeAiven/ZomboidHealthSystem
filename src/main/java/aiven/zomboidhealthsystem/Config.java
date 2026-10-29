package aiven.zomboidhealthsystem;

import net.minecraft.client.Minecraft;

import org.slf4j.Logger;

import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public final class Config {
    private static final File configDirectory = new File(Minecraft.getInstance().gameDirectory, "config");
    private static final File file = new File(configDirectory, ZomboidHealthSystem.ID_VER + ".json");

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Logger log = ZomboidHealthSystem.log;

    private static Config config;

    private Config() {

    }

    public boolean temperature = true;
    public boolean pain_keeps_awake = true;
    public boolean time_hud = true;
    public boolean health_hud = true;
    public boolean moodles_hud = true;
    public boolean show_bleeding_icon = true;
    public boolean show_injured_icon = true;

    public float temperature_multiplier = 1.0F;
    public float drowsiness_multiplier = 1.0F;
    public float thirst_multiplier = 1.0F;
    public float exhaustion_multiplier = 1.0F;
    public float wet_multiplier = 1.0F;
    public float hunger_multiplier = 1.0F;
    public float damage_multiplier = 1.0F;
    public float sleeping_pills_multiplier = 1.0F;

    public float wind_chance = 0.33F;
    public float infection_chance = 1.0F / (1.5F * 60 * 20);
    public float cold_chance = 1.0F / (9 * 60 * 20);
    public float average_bleeding_chance = 0.25F;
    public float zombification_chance = 0.25F;
    public float exhaustion_per_jump = 0.033F;

    public float min_comfortable_temp = 10.0F;
    public float max_comfortable_temp = 30.0F;

    public float heat_from_wool_helmet = 6.5F;
    public float heat_from_wool_chestplate = 8.5F;
    public float heat_from_wool_leggings = 7.5F;
    public float heat_from_wool_boots = 6.5F;

    public float heat_from_leather_helmet = 4.5F;
    public float heat_from_leather_chestplate = 6.5F;
    public float heat_from_leather_leggings = 5.0F;
    public float heat_from_leather_boots = 4.5F;

    public float heat_from_other_armor = 1.0F;

    public float max_heat_from_block = 30.0F;

    public int heal_time = 40 * 60 * 20;
    public int food_heal_amount = 2;
    public int update_frequency = 1;
    public int min_drowsiness_for_sleep = 1;
    public int moodle_icon_size = 16;
    public int moodles_hud_ypos = 90;

    public int health_hud_xpos = 10;
    public int health_hud_ypos = 30;

    public int time_hud_xpos = 6;
    public int time_hud_ypos = 6;

    public static void initialize() {
        if(file.exists()) {
            try {
                config = mapper.readValue(file, Config.class);
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
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, config);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    public static Config getInstance() {
        return config;
    }
}
