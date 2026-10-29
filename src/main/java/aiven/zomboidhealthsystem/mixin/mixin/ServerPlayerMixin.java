package aiven.zomboidhealthsystem.mixin.mixin;

import aiven.zomboidhealthsystem.network.ServerNetworking;
import aiven.zomboidhealthsystem.network.packet.ModHealthPacket;

import net.minecraft.server.level.ServerPlayer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ServerPlayer.class, priority = 10000)
public abstract class ServerPlayerMixin extends PlayerMixin {

    @Inject(at = @At("HEAD"), method = "doTick")
    public void doTick(CallbackInfo ci) {
        //TODO оптимизировать
        ServerNetworking.send(toServerPlayer(), new ModHealthPacket(zomboid_health_system$getModHealth()));
    }

    @Unique
    public final ServerPlayer toServerPlayer() {
        return (ServerPlayer) ((Object) this);
    }
}
