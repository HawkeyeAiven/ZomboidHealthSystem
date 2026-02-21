package aiven.zomboidhealthsystem.foundation.gui.screen;

import aiven.zomboidhealthsystem.ModKeyBindings;
import aiven.zomboidhealthsystem.ZomboidHealthSystem;
import aiven.zomboidhealthsystem.ZomboidHealthSystemClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import org.joml.Vector2f;

@Environment(EnvType.CLIENT)
public abstract sealed class UI extends AbstractModScreen permits UIHealth, UIInfo {
    public static Vector2f LAST_POS = new Vector2f(
            150 + ((float) (MinecraftClient.getInstance().getWindow().getScaledWidth() - 640) / 3),
            80 + ((float) (MinecraftClient.getInstance().getWindow().getScaledHeight() - 360) / 3));

    public static UI LAST_UI;

    public Vector2f pos;

    public UI(Vector2f ui) {
        pos = ui;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float tickDelta) {
        super.render(context, mouseX, mouseY, tickDelta);
    }

    @Override
    public void renderBackground(DrawContext context) {
        context.drawTexture(new Identifier(ZomboidHealthSystem.ID,"textures/gui/texture1.png"),
                (int) pos.x,
                (int) pos.y,0,0,
                (int)(498 / 2.5f),(int)(462 / 2.5f),
                (int)(498 / 2.5f),(int)(462 / 2.5f)
        );
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(keyCode == ModKeyBindings.HEALTH.getDefaultKey().getCode()){
            this.close();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void init() {
        addClickableWidget(ZomboidHealthSystemClient.HUD.getTimeWidget());
        addClickableWidget(ZomboidHealthSystemClient.HUD.getMoodlesWidget());
        addClickableWidget(ZomboidHealthSystemClient.HUD.getBodyPartsWidget());
    }

    @Override
    public void close() {
        super.close();
        LAST_POS = pos;
        LAST_UI = this;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(mouseX > pos.x && mouseX < pos.x + 8 && mouseY > pos.y && mouseY < pos.y + 8) {
            this.close();
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        double lastMouseX = mouseX - deltaX, lastMouseY = mouseY - deltaY;

        if(lastMouseX > pos.x + 8 && lastMouseX < pos.x + (int) (498 / 2.5F) && lastMouseY > pos.y && lastMouseY < pos.y + 8) {
            pos.x += (float) deltaX;
            pos.y += (float) deltaY;
            return true;
        } else {
            return false;
        }
    }
}
