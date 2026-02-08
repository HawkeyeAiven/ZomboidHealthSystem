package aiven.zomboidhealthsystem.foundation.gui.hud;

import aiven.zomboidhealthsystem.foundation.client.ClientWorldInfo;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;

public class TimeHud {
    private Vector2f pos;
    private final MinecraftClient client;
    private final ClientWorldInfo worldInfo;
    private int size = 0;

    public TimeHud(MinecraftClient client, ClientWorldInfo worldInfo, Vector2f pos) {
        this.client = client;
        this.worldInfo = worldInfo;
        this.pos = pos;
    }

    public void render(DrawContext context, float tickDelta) {
        String time = getStringTime();
        this.size = getTextRenderer().getWidth(time);
        context.drawText(
                this.client.textRenderer,
                Text.of(time),
                (int) pos.x, (int) pos.y, 0xFFffffff, true
        );
    }

    public String getStringTime() {
        return worldInfo.getHours() + (worldInfo.getMinutes() >= 10 ? ":" : ":0") + worldInfo.getMinutes();
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

    public ClientWorldInfo getWorldInfo() {
        return worldInfo;
    }
}
