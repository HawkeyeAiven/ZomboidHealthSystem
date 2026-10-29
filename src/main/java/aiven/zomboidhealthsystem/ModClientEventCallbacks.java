package aiven.zomboidhealthsystem;

import aiven.zomboidhealthsystem.gui.screen.ModScreen;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;

public final class ModClientEventCallbacks {

    private ModClientEventCallbacks() {

    }

    public static void initialize() {
        HudRenderCallback.EVENT.register((context, tickDelta) -> {
            Screen screen = Minecraft.getInstance().screen;
            if(!(screen instanceof ModScreen) && !(screen instanceof InventoryScreen)) {
                ZomboidHealthSystemClient.getHud().render(context, tickDelta);
            }
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if(ModKeyBindings.HEALTH.consumeClick()) {
                ModKeyBindings.health();
            }
        });
    }
}
