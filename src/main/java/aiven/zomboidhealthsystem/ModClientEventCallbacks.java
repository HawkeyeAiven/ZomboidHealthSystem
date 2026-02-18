package aiven.zomboidhealthsystem;

import aiven.zomboidhealthsystem.foundation.gui.hud.Hud;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;

@Environment(EnvType.CLIENT)
public class ModClientEventCallbacks {
    public static void initialize(){
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if(ModKeyBindings.HEALTH.wasPressed()) {
                ModKeyBindings.health();
            }
            if(ModKeyBindings.CRAWL.wasPressed()) {
                ModKeyBindings.crawl();
            }
        });

        HudRenderCallback.EVENT.register((context,tickDelta) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if(!client.options.debugEnabled) {
                Hud hud = ZomboidHealthSystemClient.HUD;
                if(hud != null) {
                    hud.render(context, tickDelta);
                }
            }
        });
    }
}
