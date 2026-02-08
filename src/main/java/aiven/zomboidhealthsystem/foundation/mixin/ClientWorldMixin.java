package aiven.zomboidhealthsystem.foundation.mixin;

import aiven.zomboidhealthsystem.ZomboidHealthSystemClient;
import aiven.zomboidhealthsystem.foundation.client.ClientWorldInfo;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.*;

@Environment(EnvType.CLIENT)
@Mixin(ClientWorld.class)
public abstract class ClientWorldMixin {
    @Shadow public abstract void setTime(long time);

    @Shadow @Final private ClientWorld.Properties clientWorldProperties;

    @Shadow public abstract void setTimeOfDay(long timeOfDay);

    @Unique
    int i = 0;
    /**
     * @author Aiven
     * @reason Easy
     */
    @Overwrite
    private void tickTime() {
        long d = 0;
        ClientWorldInfo worldInfo = ZomboidHealthSystemClient.WORLD_INFO;
        int multiplier = worldInfo.getDayLengthMultiplier();
        if(multiplier > 0 && i >= multiplier - 1){
            d = 1;
            i = 0;
        }
        i++;
        this.setTime(this.clientWorldProperties.getTime() + 1);
        if (this.clientWorldProperties.getGameRules().getBoolean(GameRules.DO_DAYLIGHT_CYCLE)) {
            this.setTimeOfDay(this.clientWorldProperties.getTimeOfDay() + d);
        }
    }
}
