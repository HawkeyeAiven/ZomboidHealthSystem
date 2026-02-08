package aiven.zomboidhealthsystem.foundation.gui.hud;

import aiven.zomboidhealthsystem.Config;
import aiven.zomboidhealthsystem.ZomboidHealthSystemClient;
import aiven.zomboidhealthsystem.foundation.client.ClientWorldInfo;
import aiven.zomboidhealthsystem.foundation.gui.screen.AbstractModScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

@Environment(EnvType.CLIENT)
public class Hud {
    public final TimeHud timeHud;
    private final MinecraftClient client;

    public Hud(MinecraftClient client) {
        this.timeHud = new TimeHud(client, ZomboidHealthSystemClient.WORLD_INFO, Config.TIME_HUD_POS.getValue());
        this.client = client;
    }

    public void render(DrawContext context, float tickDelta, ClientWorldInfo worldInfo) {
        if (Config.HEALTH_HUD.getValue() && !client.player.isCreative() && !client.player.isSpectator() && !(client.currentScreen instanceof AbstractModScreen)) {
            BodyPartHud.renderAllParts(context, tickDelta);
        }
        if(Config.TIME_HUD.getValue() && worldInfo != null) {
            timeHud.render(context, tickDelta);
        }
    }

    public TimeHud getTimeHud() {
        return timeHud;
    }
}
