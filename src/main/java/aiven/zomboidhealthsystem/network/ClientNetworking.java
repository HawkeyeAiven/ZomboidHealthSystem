package aiven.zomboidhealthsystem.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;

public final class ClientNetworking {

    private ClientNetworking() {

    }

    public static void send(FabricPacket packet) {
        ClientPlayNetworking.send(packet);
    }

    public static <T extends FabricPacket> PacketType<T> register(PacketType<T> type, ClientPlayNetworking.PlayPacketHandler<T> handler) {
        ClientPlayNetworking.registerGlobalReceiver(type, handler);
        return type;
    }

    public static void initialize() {
        register(ServerPacketTypes.healthPacketType, ClientPacketHandlers.HEALTH);
    }
}
