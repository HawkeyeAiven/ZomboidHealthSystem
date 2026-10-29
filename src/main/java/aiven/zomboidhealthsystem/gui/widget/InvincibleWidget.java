package aiven.zomboidhealthsystem.gui.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class InvincibleWidget extends AbstractModWidget {

    public InvincibleWidget(int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty());
    }

    @Override
    public boolean isRelativeContent() {
        return false;
    }

    @Override
    protected void renderWidget(GuiGraphics context, int mouseX, int mouseY, float tickDelta) {

    }
}
