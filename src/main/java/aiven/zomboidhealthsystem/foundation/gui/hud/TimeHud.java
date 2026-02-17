package aiven.zomboidhealthsystem.foundation.gui.hud;

import aiven.zomboidhealthsystem.foundation.utility.TimeOfDay;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;

@Environment(EnvType.CLIENT)
public class TimeHud {
    private Vector2f pos;
    private final MinecraftClient client;
    private int size = 0;

    public TimeHud(MinecraftClient client, Vector2f pos) {
        this.client = client;
        this.pos = pos;
    }

    public void render(DrawContext context, float tickDelta) {
        String time = getStringTime();
        if(time != null) {
            this.size = getTextRenderer().getWidth(time);
            context.drawText(
                    this.client.textRenderer,
                    Text.of(time),
                    (int) pos.x, (int) pos.y, 0xFFffffff, true
            );
        }
    }

    public String getStringTime() {
        if(client.world != null) {
            return new TimeOfDay(client.world.getTimeOfDay()).getModTime();
        } else {
            return null;
        }
    }

    public int getSize() {
        return size;
    }

    public TextRenderer getTextRenderer() {
        return this.client.textRenderer;
    }

    public Vector2f getPos() {
        return pos;
    }

    public void setPos(@NotNull Vector2f pos) {
        this.pos = pos;
    }
}