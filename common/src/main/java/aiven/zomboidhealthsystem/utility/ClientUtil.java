package aiven.zomboidhealthsystem.utility;

import aiven.zomboidhealthsystem.mixin.access.PlayerAccess;
import aiven.zomboidhealthsystem.health.Health;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

public class ClientUtil {
    public static Health getHealth() {
        LocalPlayer player = Minecraft.getInstance().player;
        if(player == null) {
            return null;
        }
        return ((PlayerAccess) player).zomboid_health_system$getModHealth();
    }
}
