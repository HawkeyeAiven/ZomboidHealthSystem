package aiven.zomboidhealthsystem.gui;


import net.minecraft.client.gui.GuiGraphics;

public final class RenderHelper {

    private RenderHelper() {

    }

    public static void drawQuad(GuiGraphics drawContext, int x, int y, int width, int height, int color, int borderColor) {
        drawContext.fill(x, y, x + width, y + height, color);
        drawContext.renderOutline(x, y, width, height, borderColor);
    }
}