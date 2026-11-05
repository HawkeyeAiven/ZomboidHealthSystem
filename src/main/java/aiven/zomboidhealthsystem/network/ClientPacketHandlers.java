package aiven.zomboidhealthsystem.network;

import aiven.zomboidhealthsystem.ZomboidHealthSystem;
import aiven.zomboidhealthsystem.health.Health;
import aiven.zomboidhealthsystem.mixin.access.PlayerAccess;
import aiven.zomboidhealthsystem.network.packet.HealthPacket;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

final class ClientPacketHandlers {

    private ClientPacketHandlers() {

    }


    public static ClientPlayNetworking.PlayPacketHandler<HealthPacket> HEALTH = (packet, player, responseSender) -> {
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
    };
}
