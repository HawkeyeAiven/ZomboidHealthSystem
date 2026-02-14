package aiven.zomboidhealthsystem.foundation.world;

import aiven.zomboidhealthsystem.Config;
import aiven.zomboidhealthsystem.foundation.utility.Util;
import aiven.zomboidhealthsystem.infrastructure.config.JsonBuilder;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public final class Weather {
    private WorldSettings worldSettings;
    private float worldTemperature;
    private ServerWorld world;
    private float wind;
    private boolean newDay = false;
    private Integer sleep = null;
    private int i = 0;

    public Weather(ServerWorld world, WorldSettings worldSettings) {
        this.world = world;
        this.worldSettings = worldSettings;
    }

    public void tick() {
        if(i++ > 20 - 1 && this.worldSettings.hasTemperature() && getWorld() != null){
            this.update();
            i = 0;
        }
    }

    private void update() {
        long timeOfDay = this.getWorld().getTimeOfDay() % 24000;

        if(timeOfDay < 20 * 2L){
            if(!newDay){
                this.onNewDay();
                newDay = true;
            }
        } else {
            newDay = false;
        }

        if(!getWorld().getGameRules().get(GameRules.DO_DAYLIGHT_CYCLE).get()){
            return;
        }

        if(worldSettings.hasTemperature()) {
            worldTemperature = getTemperatureOnTimeOfDay(20, (int) timeOfDay);
        }
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

    private float getTemperatureOnTimeOfDay(int tickDelta, int timeOfDay) {
        float temperature = this.worldTemperature;
        float average_temperature = getSeasonTemperature();
        float amount = 1.0F / (60 * 20) * tickDelta / worldSettings.getDayLengthMultiplier();
        float divisor = (float) (Math.max( Math.pow(Math.abs((average_temperature - worldTemperature) / 2), 2) , 1 ));

        if(timeOfDay > 10000){
            temperature -= temperature < average_temperature ? amount / divisor * Config.COOLING_WORLD_MULTIPLIER.getValue(): amount * Config.COOLING_WORLD_MULTIPLIER.getValue();
        } else {
            temperature += temperature > average_temperature ? amount / divisor * Config.HEATING_WORLD_MULTIPLIER.getValue(): amount * Config.HEATING_WORLD_MULTIPLIER.getValue();
        }

        if(world.isRaining()) {
            if(temperature > average_temperature - 5) {
                temperature -= (temperature < average_temperature ? amount / divisor : amount) * 1.5F;
            }
        }

        return temperature;
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

    public void onSleep() {
        if(sleep != null){
            int ticks = 24000 - sleep;
            int timeOfDay = sleep;
            sleep = null;

            worldTemperature = getTemperatureOnTimeOfDay(ticks, timeOfDay);
        }
    }


    public float getSeasonTemperature(){
        return getSeasonTemperature(this.worldSettings, getWorld());
    }

    public float getWorldTemperature() {
        return worldTemperature;
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

    public void setSleep(long sleep) {
        this.sleep = (int) (sleep % 24000);
    }

    public void setWorldTemperature(float worldTemperature) {
        this.worldTemperature = worldTemperature;
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

    @Override
    public String toString() {
        JsonBuilder builder = new JsonBuilder();
        builder.append("world_temperature", String.valueOf(this.getWorldTemperature()));
        builder.append("wind", String.valueOf(this.getWind()));
        return builder.toString();
    }

    public static float getSeasonTemperature(WorldSettings settings, @Nullable World world) {
        int durationYear = settings.getDaysInSeason() * 24000 * 4;
        int duration = settings.getDaysInSeason() * 24000;
        int time;
        if(world != null) {
            time = settings.getStartTicks() + (int)(world.getTime() / settings.getDayLengthMultiplier());
        } else {
            time = settings.getStartTicks();
        }
        float MAX_TEMP = Config.MAX_SEASON_TEMPERATURE.getValue();

        time  %= durationYear;
        if(time >= duration / 2){
            time -= duration / 2 + 1;
        } else {
            time = (durationYear - (duration / 2)) + (time) + 1;
        }
        float temp;
        int d;
        float divisor = 27 / MAX_TEMP;
        if(time > durationYear / 2){
            time %= durationYear / 2;
            d = time / (duration / 28);
            temp = MAX_TEMP * -1;
            temp += (float) d / divisor;
        } else {
            time %= durationYear / 2;
            d = time / (duration / 28);
            temp = MAX_TEMP;
            temp -= (float) d / divisor;
        }

        return temp;
    }
}