package aiven.zomboidhealthsystem;

import aiven.zomboidhealthsystem.foundation.client.ClientHealth;
import aiven.zomboidhealthsystem.foundation.client.ClientWorldInfo;
import aiven.zomboidhealthsystem.foundation.network.ClientTasks;
import aiven.zomboidhealthsystem.foundation.network.ServerPackets;
import aiven.zomboidhealthsystem.foundation.gui.hud.Hud;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import aiven.zomboidhealthsystem.Config;

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
    public static ClientHealth HEALTH = new ClientHealth();
    public static Hud HUD = new Hud(MinecraftClient.getInstance());
}
