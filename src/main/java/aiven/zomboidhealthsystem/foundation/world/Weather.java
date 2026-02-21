package aiven.zomboidhealthsystem.foundation.world;

import aiven.zomboidhealthsystem.Config;
import aiven.zomboidhealthsystem.foundation.utility.Util;
import aiven.zomboidhealthsystem.infrastructure.config.JsonBuilder;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import java.util.Random;

public final class Weather {
    private static final int UPDATE_FREQUENCY = 20;

    private WorldSettings worldSettings;
    private ServerWorld world;
    private float wind;
    private boolean newDay = false;
    private long lastTimeOfDay = -1;
    private float temperatureFromRain = 0;
    private int i = 0;

    public Weather(ServerWorld world, WorldSettings worldSettings) {
        this.world = world;
        this.worldSettings = worldSettings;
    }

    public void tick() {
        if(i++ > UPDATE_FREQUENCY - 1 && getWorld() != null){
            this.update();
            i = 0;
        }
    }

    private void update() {
        long timeOfDay = this.getWorld().getTimeOfDay();

        if(timeOfDay % 24000 < 1000){
            if(!newDay){
                this.onNewDay();
                newDay = true;
            }
        } else {
            newDay = false;
        }

        float d = (float) (lastTimeOfDay == -1 ? 1 : timeOfDay - lastTimeOfDay);

        if(d > 0) {
            if (world.isRaining()) {
                temperatureFromRain = Math.max(temperatureFromRain - d / 1000, -5);
            } else {
                temperatureFromRain = Math.min(temperatureFromRain + d / 1000, 0);
            }
        }

        lastTimeOfDay = timeOfDay;
    }

    private void onNewDay() {
        if(worldSettings.hasWind()) {
            if (Util.random(Config.WIND_CHANCE.getValue())) {
                wind = new Random().nextFloat(3.0F, 10.0F);
            } else {
                wind = 0;
            }
        }
    }

    public float getWorldTemperature() {
        return getSeasonTemperature() + getTemperatureOfTimeOfDay() + getTemperatureFromRain();
    }

    public float getTemperatureAtPos(BlockPos pos) {
        return getTemperatureAtPos(pos, getWorld().getBiome(pos).value());
    }

    public float getTemperatureAtPos(BlockPos pos, Biome biome) {
        float temperatureBiome = biome.getTemperature();
        float height = pos.getY() - 63;
        float d;
        if(Config.TEMPERATURE_DEPENDS_ON_Z_COORDINATE.getValue()) {
            d = (float) pos.getZ() / 1000;
        } else {
            d = 0;
        }
        float t = getWorldTemperature() - (height / 25) + d;
        if(temperatureBiome > 0.5f){
            t += 10 * temperatureBiome;
        } else if (temperatureBiome < 0.15f) {
            temperatureBiome -= 0.15f;
            temperatureBiome *= 3;
            t += 15 * temperatureBiome;
        }

        return t;
    }

    public boolean doesNotSnow(BlockPos pos, Biome biome) {
        return this.getTemperatureAtPos(pos, biome) > 0;
    }

    public float getSeasonTemperature() {
        return getSeasonTemperature(this.worldSettings.getDaysInSeason(), this.getWorldSettings().getStartTicks() + this.getWorld().getTimeOfDay());
    }

    public World getWorld() {
        return world;
    }

    public float getWind() {
        return wind;
    }

    public void setWind(float wing) {
        this.wind = wing;
    }

    public void setWorld(ServerWorld world) {
        this.world = world;
    }

    public void setWorldSettings(WorldSettings worldSettings) {
        this.worldSettings = worldSettings;
    }

    public WorldSettings getWorldSettings() {
        return worldSettings;
    }

    public void setTemperatureFromRain(float temperatureFromRain) {
        this.temperatureFromRain = temperatureFromRain;
    }

    public float getTemperatureFromRain() {
        return temperatureFromRain;
    }

    @Override
    public String toString() {
        JsonBuilder builder = new JsonBuilder();
        builder.append("temperature_from_rain", String.valueOf(this.temperatureFromRain));
        builder.append("wind", String.valueOf(this.getWind()));
        return builder.toString();
    }

    public float getTemperatureOfTimeOfDay() {
        return getTemperatureOfTimeOfDay(this.getWorld().getTimeOfDay());
    }

    public static float getTemperatureOfTimeOfDay(long timeOfDay) {
        timeOfDay += 1000;
        timeOfDay = timeOfDay % 24000;

        if(timeOfDay >= 0 && timeOfDay <= 7000) {
            return (float) timeOfDay / 700 - 5.0F;
        } else {
            int d = (int) timeOfDay - 7000;
            return (float) d / -1700 + 5.0F;
        }
    }

    public static float getSeasonTemperature(int daysInSeason, long time) {
        int yearDuration = daysInSeason * 24000 * 4;
        int seasonDuration = daysInSeason * 24000;
        float MAX_TEMP = Config.MAX_SEASON_TEMPERATURE.getValue();
        time  %= yearDuration;

        if(time >= seasonDuration / 2){
            time -= seasonDuration / 2 + 1;
        } else {
            time = (yearDuration - (seasonDuration / 2)) + (time) + 1;
        }
        float temp;
        long d;
        float divisor = 27 / MAX_TEMP;
        if(time > yearDuration / 2){
            time %= yearDuration / 2;
            d = time / (seasonDuration / 28);
            temp = MAX_TEMP * -1;
            temp += (float) d / divisor;
        } else {
            time %= yearDuration / 2;
            d = time / (seasonDuration / 28);
            temp = MAX_TEMP;
            temp -= (float) d / divisor;
        }

        return temp;
    }
}