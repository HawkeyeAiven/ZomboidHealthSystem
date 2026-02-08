package aiven.zomboidhealthsystem.foundation.client;

import aiven.zomboidhealthsystem.ZomboidHealthSystemClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.PacketByteBuf;

public class ClientWorldInfo {
    private int dayLengthMultiplier = 3;

    private int hours = 0;
    private int minutes = 0;
    private int seconds = 0;

    private float temperature = 20;
    private float worldTemp = 20;
    private float seasonTemp = 20;

    private float wind = 0;

    private long ticksFromStart = 0;

    public ClientWorldInfo() {

    }

    public void onPacket(PacketByteBuf packetByteBuf) {
        int[] temp = packetByteBuf.readIntArray();
        boolean hasTemp = packetByteBuf.readBoolean();

        ClientWorldInfo worldInfo = ZomboidHealthSystemClient.WORLD_INFO;

        if(hasTemp) {
            worldInfo.setTemperature((float) temp[0] / 10);
            worldInfo.setWorldTemp((float) temp[1] / 10);
            worldInfo.setSeasonTemp((float) temp[2] / 10);
            worldInfo.setWind((float) temp[3] / 10);
        } else {
            worldInfo.setTemperature(0);
            worldInfo.setWorldTemp(0);
            worldInfo.setSeasonTemp(0);
            worldInfo.setWind(0);
        }
        setDayLengthMultiplier(temp[4]);
    }

    public void tick(ClientWorld world) {
        setSeconds((int) (world.getTimeOfDay() * 3.6f));
        setMinutes(getSeconds() / 60 % 60);
        setHours((getSeconds() / 3600 + 8) % 24);
        setTicksFromStart(world.getTime());
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public void setDayLengthMultiplier(int dayLengthMultiplier) {
        this.dayLengthMultiplier = dayLengthMultiplier;
    }

    public void setSeasonTemp(float seasonTemp) {
        this.seasonTemp = seasonTemp;
    }

    public void setWorldTemp(float worldTemp) {
        this.worldTemp = worldTemp;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    public void setTicksFromStart(long ticksFromStart) {
        this.ticksFromStart = ticksFromStart;
    }

    public float getSeasonTemp() {
        return seasonTemp;
    }

    public void setWind(float wind) {
        this.wind = wind;
    }

    public int getDayLengthMultiplier() {
        return dayLengthMultiplier;
    }

    public float getTemperature() {
        return temperature;
    }

    public float getWorldTemp() {
        return worldTemp;
    }

    public int getHours() {
        return hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public int getSeconds() {
        return seconds;
    }

    public long getTicksFromStart() {
        return ticksFromStart;
    }

    public float getWind() {
        return wind;
    }
}
