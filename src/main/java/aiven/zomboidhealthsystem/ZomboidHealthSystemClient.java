package aiven.zomboidhealthsystem;

import aiven.zomboidhealthsystem.gui.widget.HudWidgetGroup;
import aiven.zomboidhealthsystem.network.ClientNetworking;

import net.fabricmc.api.ClientModInitializer;

import net.minecraft.client.Minecraft;

public final class ZomboidHealthSystemClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        hud = new HudWidgetGroup(Minecraft.getInstance());

        ModClientEventCallbacks.initialize();
        ModKeyBindings.initialize();
        ClientNetworking.initialize();
    }

    private static HudWidgetGroup hud;

    public static HudWidgetGroup getHud() {
        return hud;
    }
}
