package aiven.zomboidhealthsystem.foundation.mixin;

import aiven.zomboidhealthsystem.foundation.world.ModServer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.GameRules;
import net.minecraft.world.level.ServerWorldProperties;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ServerWorld.class)
public abstract class ServerWorldMixin {

    @Shadow @Final private boolean shouldTickTime;

    @Shadow @Final private ServerWorldProperties worldProperties;

    @Shadow public abstract void setTimeOfDay(long timeOfDay);

    @Shadow @Final private MinecraftServer server;

    @Shadow @NotNull public abstract MinecraftServer getServer();

    @Shadow public abstract String toString();

    @Inject(at = @At("HEAD"), method = "onPlayerRespawned")
    private void onPlayerRespawned(ServerPlayerEntity player, CallbackInfo ci) {
        ModServer.sendPacketRespawn(player);
    }

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