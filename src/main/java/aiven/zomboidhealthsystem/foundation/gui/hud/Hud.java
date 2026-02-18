package aiven.zomboidhealthsystem.foundation.gui.hud;

import aiven.zomboidhealthsystem.Config;
import aiven.zomboidhealthsystem.foundation.gui.screen.AbstractModScreen;
import aiven.zomboidhealthsystem.foundation.gui.widget.BodyPartsWidget;
import aiven.zomboidhealthsystem.foundation.gui.widget.MoodlesWidget;
import aiven.zomboidhealthsystem.foundation.gui.widget.TimeWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

@Environment(EnvType.CLIENT)
public class Hud {
    private final TimeWidget timeWidget;
    private final MoodlesWidget moodlesWidget;
    private final BodyPartsWidget bodyPartsWidget;
    private final MinecraftClient client;

    public Hud(MinecraftClient client) {
        this.timeWidget = new TimeWidget();
        this.moodlesWidget = new MoodlesWidget();
        this.bodyPartsWidget = new BodyPartsWidget();
        this.client = client;
    }

    public void render(DrawContext context, float tickDelta) {
        if(!(client.currentScreen instanceof AbstractModScreen)) {
            if (Config.HEALTH_HUD.getValue() && !client.player.isCreative() && !client.player.isSpectator()) {
                this.bodyPartsWidget.render(context, -1, -1, tickDelta);
            }
            if (Config.TIME_HUD.getValue()) {
                this.timeWidget.render(context, -1, -1, tickDelta);
            }
            this.moodlesWidget.render(context, -1, -1, tickDelta);
        }
    }

    public TimeWidget getTimeWidget() {
        return timeWidget;
    }

    public MoodlesWidget getMoodlesWidget() {
        return moodlesWidget;
    }

    public BodyPartsWidget getBodyPartsWidget() {
        return bodyPartsWidget;
    }
}
