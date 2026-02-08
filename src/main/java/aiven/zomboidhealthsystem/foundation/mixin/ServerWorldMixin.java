package aiven.zomboidhealthsystem.foundation.mixin;

import aiven.zomboidhealthsystem.foundation.world.ModServer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.GameRules;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.level.ServerWorldProperties;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.*;

@Mixin(value = ServerWorld.class, priority = 2000)
public abstract class ServerWorldMixin {

    @Shadow @Final private boolean shouldTickTime;

    @Shadow @Final private ServerWorldProperties worldProperties;

    @Shadow public abstract void setTimeOfDay(long timeOfDay);

    @Shadow @Final private MinecraftServer server;

    @Shadow @NotNull public abstract MinecraftServer getServer();

    @Shadow public abstract String toString();

    @Shadow public abstract ServerWorld toServerWorld();

    @Shadow public abstract PersistentStateManager getPersistentStateManager();

    @Unique
    private int i = 0;

    /**
     * @author Aiven
     * @reason The Minecraft day duration is very short
     */
    @Overwrite
    public void tickTime()  {
        int dayLengthMultiplier = ModServer.WORLD_SETTINGS.getDayLengthMultiplier();
        long d = 0;
        if(dayLengthMultiplier > 0 && i >= dayLengthMultiplier - 1) {
            d = 1;
            i = 0;
        }
        i++;
        if (this.shouldTickTime) {
            long l = this.worldProperties.getTime() + 1;
            this.worldProperties.setTime(l);
            this.worldProperties.getScheduledEvents().processEvents(this.server, l);
            if (this.worldProperties.getGameRules().getBoolean(GameRules.DO_DAYLIGHT_CYCLE)) {
                this.setTimeOfDay(this.worldProperties.getTimeOfDay() + d);
            }
        }
    }
}