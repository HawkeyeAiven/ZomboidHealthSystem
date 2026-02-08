package aiven.zomboidhealthsystem.foundation.network;

import aiven.zomboidhealthsystem.foundation.network.packet.ClientPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class ServerNetwork {
    public static void registerPacket(Identifier id, ServerPlayNetworking.PlayChannelHandler handler){
        ClientPacket packet = new ClientPacket(id.getPath(),handler);
        ServerPlayNetworking.registerGlobalReceiver(packet.ID,handler);
    }

    public static void send(Identifier id, ServerPlayerEntity player, PacketByteBuf buf){
        ServerPlayNetworking.send(player, id, buf);
    }
}
