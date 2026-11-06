package aiven.zomboidhealthsystem;

import aiven.zomboidhealthsystem.gui.widget.HudWidgetGroup;
import aiven.zomboidhealthsystem.network.ClientNetworking;

import net.minecraft.client.Minecraft;

public final class ZomboidHealthSystemClient {

    private static HudWidgetGroup hud;

    private ZomboidHealthSystemClient() {

    }

    public static void initialize() {
        hud = new HudWidgetGroup(Minecraft.getInstance());

        ModKeyBindings.initialize();
        ClientNetworking.initialize();
    }

    public static HudWidgetGroup getHud() {
        return hud;
    }
}
