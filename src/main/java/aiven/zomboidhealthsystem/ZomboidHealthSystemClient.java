package aiven.zomboidhealthsystem;

import aiven.zomboidhealthsystem.foundation.client.ClientWorldInfo;
import aiven.zomboidhealthsystem.foundation.network.ClientTasks;
import aiven.zomboidhealthsystem.foundation.network.ServerPackets;
import aiven.zomboidhealthsystem.foundation.gui.hud.Hud;
import aiven.zomboidhealthsystem.foundation.player.Health;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;

@Environment(EnvType.CLIENT)
public class ZomboidHealthSystemClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ServerPackets.initialize();
        ModKeyBindings.initialize();
        ModClientEventCallbacks.initialize();
        ClientTasks.initialize();
    }

    public static ClientWorldInfo WORLD_INFO = new ClientWorldInfo();
    public static Health HEALTH = new Health(null);
    public static Hud HUD = new Hud(MinecraftClient.getInstance());
}
