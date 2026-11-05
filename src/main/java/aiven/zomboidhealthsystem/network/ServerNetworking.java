package aiven.zomboidhealthsystem.network;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import net.minecraft.server.level.ServerPlayer;

public final class ServerNetworking {

    private ServerNetworking() {

    }

    public static void send(ServerPlayer player, FabricPacket packet) {
        ServerPlayNetworking.send(player, packet);
    }

    public static <T extends FabricPacket> PacketType<T> register(PacketType<T> type, ServerPlayNetworking.PlayPacketHandler<T> handler) {
        ServerPlayNetworking.registerGlobalReceiver(type, handler);
        return type;
    }

    public static void initialize() {
        register(ClientPacketTypes.actionPacketType, ServerPacketHandlers.ACTION);
    }
}
