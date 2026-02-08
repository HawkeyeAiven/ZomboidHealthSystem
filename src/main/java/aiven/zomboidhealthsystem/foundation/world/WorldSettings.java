package aiven.zomboidhealthsystem.foundation.world;

import aiven.zomboidhealthsystem.infrastructure.config.JsonBuilder;

public class WorldSettings {
    private final int dayLengthMultiplier;
    private final int daysInSeason;
    private final int startTicks;
    private final boolean temperature;
    private final boolean wind;

    public WorldSettings(int dayLengthMultiplier, int daysInSeason, int startTicks, boolean temperature, boolean wind){
        this.dayLengthMultiplier = dayLengthMultiplier;
        this.daysInSeason = daysInSeason;
        this.startTicks = startTicks;
        this.temperature = temperature;
        this.wind = wind;
    }

    public int getDaysInSeason() {
        return daysInSeason;
    }

    public int getStartTicks() {
        return startTicks;
    }

    public int getDayLengthMultiplier() {
        return dayLengthMultiplier;
    }

    public boolean hasTemperature() {
        return temperature;
    }

    public boolean hasWind(){
        return wind;
    }

    @Override
    public String toString() {
        JsonBuilder builder = new JsonBuilder();
        builder.append("day_length_multiplier", String.valueOf(dayLengthMultiplier));
        builder.append("days_in_season", String.valueOf(daysInSeason));
        builder.append("start_ticks", String.valueOf(startTicks));
        builder.append("temperature", String.valueOf(temperature));
        builder.append("wind", String.valueOf(wind));
        return builder.toString();
    }
}
