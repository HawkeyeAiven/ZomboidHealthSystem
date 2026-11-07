package aiven.zomboidhealthsystem.mixin.access;

public interface PrimaryLevelDataAccess {
    float zomboid_health_system$getWind();

    void zomboid_health_system$setWind(float value);

    float zomboidhealthsystem$getRainTemperature();

    void zomboidhealthsystem$setRainTemperature(float value);
}
