package aiven.zomboidhealthsystem.network.fabric;

import aiven.zomboidhealthsystem.network.ServerPacketHandler;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import net.minecraft.server.level.ServerPlayer;

public final class ServerNetworkingImpl {

    private ServerNetworkingImpl() {

    }

    public static void send(ServerPlayer player, Object packet) {
        ServerPlayNetworking.send(player, (FabricPacket) packet);
    }

    public static <T extends FabricPacket> PacketType<T> register(PacketType<T> type, ServerPlayNetworking.PlayPacketHandler<T> handler) {
        ServerPlayNetworking.registerGlobalReceiver(type, handler);
        return type;
    }

    public static void initialize() {
        register(ClientPacketTypes.actionPacketType, (packet, player, responseSender) -> {
            try {
                ServerPacketHandler.onActionPacket(player, packet.getActionType(), packet.getBodyPartId(), packet.getIndex());
            } catch (Exception e) {
            }
        });
    }
}
