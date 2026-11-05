package aiven.zomboidhealthsystem.health.moodles;

import aiven.zomboidhealthsystem.mixin.access.ServerLevelAccess;
import aiven.zomboidhealthsystem.health.Health;
import aiven.zomboidhealthsystem.utility.Util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Pose;

public final class Wind extends SimpleMoodle {
    private float delta = 0.0F;

    public Wind(Health health) {
        super(health);
    }

    @Override
    public String getId() {
        return "wind";
    }

    @Override
    public void update(float tickDelta) {
        if(tickDelta != 0) {
            delta += tickDelta;

            if(delta >= 5) {
                delta = 0;
                value = getWind();
            }
            getHealth().getTemperature().setHeat(this, -value * (getHealth().getWet().getValue() / 2 + 1));
        }
    }

    @Override
    public int getAmplifier() {
        return (int) value / 3;
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
