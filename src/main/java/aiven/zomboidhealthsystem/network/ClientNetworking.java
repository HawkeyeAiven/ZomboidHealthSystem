package aiven.zomboidhealthsystem.network;

import aiven.zomboidhealthsystem.ZomboidHealthSystem;
import aiven.zomboidhealthsystem.mixin.access.PlayerAccess;
import aiven.zomboidhealthsystem.health.Health;

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
        register(ServerPacketTypes.healthPacketType, (packet, player, responseSender) -> {
            try {
                if (player instanceof PlayerAccess playerAccess) {
                    if (playerAccess.zomboid_health_system$getModHealth() == null) {
                        playerAccess.zomboid_health_system$setModHealth(Health.parseHealth(player, packet.getBytes()));
                    } else {
                        Health health = playerAccess.zomboid_health_system$getModHealth();
                        health.readNbt(packet.getBytes());
                    }
                }
            } catch (Exception e) {
                ZomboidHealthSystem.log.error("Failed to read health packet type", e);
            }
        });
    }
}
