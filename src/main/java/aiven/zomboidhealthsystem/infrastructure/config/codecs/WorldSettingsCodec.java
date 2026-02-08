package aiven.zomboidhealthsystem.infrastructure.config.codecs;

import aiven.zomboidhealthsystem.foundation.world.WorldSettings;
import aiven.zomboidhealthsystem.infrastructure.config.Json;

public class WorldSettingsCodec extends Codec {
    public WorldSettingsCodec(String key, WorldSettings value) {
        super(key, value);
    }

    @Override
    public WorldSettings getValue() {
        return (WorldSettings) super.getValue();
    }

    @Override
    public boolean setParsedValue(String string) {
        try {
            Json json = new Json();
            json.load(string);
            int dayLengthMultiplier = Integer.parseInt(json.getValue("day_length_multiplier"));
            int daysInSeason = Integer.parseInt(json.getValue("days_in_season"));
            int ticks = Integer.parseInt(json.getValue("start_ticks"));
            boolean temperature = Boolean.parseBoolean(json.getValue("temperature"));
            boolean wind = Boolean.parseBoolean(json.getValue("wind"));
            setValue(new WorldSettings(dayLengthMultiplier, daysInSeason, ticks, temperature, wind));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
