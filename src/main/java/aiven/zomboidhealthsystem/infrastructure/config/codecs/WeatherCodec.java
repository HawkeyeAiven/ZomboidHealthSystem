package aiven.zomboidhealthsystem.infrastructure.config.codecs;

import aiven.zomboidhealthsystem.foundation.world.Weather;
import aiven.zomboidhealthsystem.infrastructure.config.Json;

public class WeatherCodec extends Codec {
    public WeatherCodec(String key, Weather value) {
        super(key, value);
    }

    @Override
    public Weather getValue() {
        return (Weather) super.getValue();
    }

    @Override
    public boolean setParsedValue(String string) {
        try {
            Weather weather = new Weather(null, null);
            Json json = new Json();
            json.load(string);
            weather.setWorldTemperature(Float.parseFloat(json.getValue("world_temperature")));
            weather.setWind(Float.parseFloat(json.getValue("wind")));
            setValue(weather);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
