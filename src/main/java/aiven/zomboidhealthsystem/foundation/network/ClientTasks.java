package aiven.zomboidhealthsystem.foundation.network;

import aiven.zomboidhealthsystem.ZomboidHealthSystemClient;
import aiven.zomboidhealthsystem.foundation.player.Health;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

@Environment(EnvType.CLIENT)
public enum ClientTasks {
    DAMAGE((minecraftClient, clientPlayNetworkHandler, packetByteBuf, packetSender) -> {
        minecraftClient.player.animateDamage(30);
    }),
    HEALTH((minecraftClient, clientPlayNetworkHandler, packetByteBuf, packetSender) -> {
        Health clientHealth = ZomboidHealthSystemClient.HEALTH;

        clientHealth.set(packetByteBuf.readString());
    }),
    WORLD((minecraftClient, clientPlayNetworkHandler, packetByteBuf, packetSender) -> {
        ZomboidHealthSystemClient.WORLD_INFO.onPacket(packetByteBuf);
    })
    ;

    private final ClientPlayNetworking.PlayChannelHandler task;

    ClientTasks(ClientPlayNetworking.PlayChannelHandler task){
        this.task=task;
    }

    public ClientPlayNetworking.PlayChannelHandler get(){
        return this.task;
    }

    public static void initialize(){}
}
