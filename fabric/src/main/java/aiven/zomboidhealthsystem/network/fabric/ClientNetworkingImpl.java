package aiven.zomboidhealthsystem.network.fabric;

import aiven.zomboidhealthsystem.network.fabric.packet.HealthPacket;
import aiven.zomboidhealthsystem.network.ClientPacketHandler;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;

import net.minecraft.client.player.LocalPlayer;

public final class ClientNetworkingImpl {

    private ClientNetworkingImpl() {

    }

    public static void send(Object packet) {
        ClientPlayNetworking.send((FabricPacket) packet);
    }

    public static void initialize() {
        register(ServerPacketTypes.healthPacketType, new ClientPlayNetworking.PlayPacketHandler<HealthPacket>() {
            @Override
            public void receive(HealthPacket packet, LocalPlayer player, PacketSender responseSender) {
                try {
                    ClientPacketHandler.onHealthPacket(packet.getBytes());
                } catch (Exception e) {
                }
            }
        });
    }

    private static <T extends FabricPacket> PacketType<T> register(PacketType<T> type, ClientPlayNetworking.PlayPacketHandler<T> handler) {
        ClientPlayNetworking.registerGlobalReceiver(type, handler);
        return type;
    }
}
