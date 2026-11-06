package aiven.zomboidhealthsystem.mixin.mixin;

import aiven.zomboidhealthsystem.Config;
import aiven.zomboidhealthsystem.network.ServerNetworking;
import aiven.zomboidhealthsystem.network.ServerPacketFactory;

import net.minecraft.server.level.ServerPlayer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ServerPlayer.class, priority = 10000)
public abstract class ServerPlayerMixin extends PlayerMixin {

    @Unique
    private int zomboidhealthsystem$i = 0;

    @Inject(at = @At("HEAD"), method = "doTick")
    public void doTick(CallbackInfo ci) {
        if(zomboidhealthsystem$i++ >= Config.getInstance().update_frequency - 1) {
            ServerNetworking.send(zomboidhealthsystem$toServerPlayer(), ServerPacketFactory.createHealthPacket(zomboid_health_system$getModHealth()));
        }
    }

    @Unique
    public final ServerPlayer zomboidhealthsystem$toServerPlayer() {
        return (ServerPlayer) ((Object) this);
    }
}
