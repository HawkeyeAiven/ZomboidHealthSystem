package aiven.zomboidhealthsystem.network;

import aiven.zomboidhealthsystem.health.Health;
import aiven.zomboidhealthsystem.mixin.access.PlayerAccess;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

public final class ClientPacketHandler {

    private ClientPacketHandler() {

    }

    public static void onHealthPacket(byte[] packet) {
        LocalPlayer player = Minecraft.getInstance().player;
        if(player != null) {
            PlayerAccess playerAccess = (PlayerAccess) player;
            if(playerAccess.zomboid_health_system$getModHealth() != null) {
                playerAccess.zomboid_health_system$getModHealth().readNbt(packet);
            } else {
                playerAccess.zomboid_health_system$setModHealth(Health.parseHealth(player, packet));
            }
        }
    }
}
