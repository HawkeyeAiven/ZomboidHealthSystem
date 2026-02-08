package aiven.zomboidhealthsystem.foundation.network;

import aiven.zomboidhealthsystem.foundation.network.packet.ServerPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ClientNetwork {
    public static void registerPacket(Identifier id, ClientPlayNetworking.PlayChannelHandler handler){
        ServerPacket packet = new ServerPacket(id.getPath(),handler);
        ClientPlayNetworking.registerGlobalReceiver(packet.ID,handler);
    }

    public static void send(Identifier id, PacketByteBuf buf){
        ClientPlayNetworking.send(id, buf);
    }
}
