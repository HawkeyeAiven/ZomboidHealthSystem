package aiven.zomboidhealthsystem.foundation.player.moodles;

import aiven.zomboidhealthsystem.foundation.player.Health;
import aiven.zomboidhealthsystem.foundation.utility.Util;
import aiven.zomboidhealthsystem.foundation.world.ModServer;
import net.minecraft.entity.EntityPose;
import net.minecraft.util.math.BlockPos;

public class Wind extends Moodle {
    public Wind(Health health) {
        super(health);
    }

    @Override
    public void update() {
        setAmount(getWind());

        getHealth().getTemperature().addHeat(-getAmount() * (getHealth().getWet().getAmount() / 2 + 1));
    }

    private float getWind() {
        if(ModServer.WEATHER.getWind() > 0 && isOverWorld()) {
            BlockPos pos = getPlayer().getBlockPos();
            if(getPlayer().getPose() != EntityPose.SWIMMING) {
                pos = pos.up(1);
            }

            if(Util.isInOpenSpace(getPlayer().getWorld(), pos)) {
                return ModServer.WEATHER.getWind();
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }

    @Override
    public String getId() {
        return "wind";
    }

    @Override
    public void readNbt(String value) {
    }

    @Override
    public String getNbt() {
        return null;
    }
}
