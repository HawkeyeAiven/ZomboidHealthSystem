package aiven.zomboidhealthsystem.mixin.access;

import aiven.zomboidhealthsystem.health.Health;

public interface PlayerAccess {
    Health zomboid_health_system$getModHealth();

    void zomboid_health_system$setModHealth(Health health);
}
