package aiven.zomboidhealthsystem.health.moodles;

import aiven.zomboidhealthsystem.mixin.access.ServerLevelAccess;
import aiven.zomboidhealthsystem.health.Health;
import aiven.zomboidhealthsystem.utility.Util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Pose;

public final class Wind extends SimpleMoodle {
    private float amount = 0;

    public Wind(Health health) {
        super(health);
    }

    @Override
    public String getId() {
        return "wind";
    }

    @Override
    public void update() {
        amount = getWind();

        getHealth().getTemperature().addHeat(-amount * (getHealth().getWet().getValue() / 2 + 1));
    }

    @Override
    public int getAmplifier() {
        return Math.max((int) (amount / 5 - 1), 0);
    }

    @Override
    public void sleep(int sumTicks) {

    }

    private float getWind() {
        float wind = ((ServerLevelAccess) getLevel()).zomboid_health_system$getWind();
        if(wind > 0) {
            BlockPos pos = getPlayer().blockPosition();
            if(getPlayer().getPose() != Pose.SWIMMING) {
                pos = pos.above(1);
            }

            if(Util.isInOpenSpace(getLevel(), pos)) {
                return wind;
            }
        }
        return 0;
    }
}
