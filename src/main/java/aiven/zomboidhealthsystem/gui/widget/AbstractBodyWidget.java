package aiven.zomboidhealthsystem.gui.widget;

import aiven.zomboidhealthsystem.gui.screen.ModScreen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public abstract class AbstractBodyWidget extends AbstractMovableWidget {

    public AbstractBodyWidget(int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float tickDelta) {
        if(Minecraft.getInstance().player.isSpectator()) {
            return;
        }

        if(Minecraft.getInstance().player.isCreative() && !(Minecraft.getInstance().screen instanceof ModScreen)) {
            return;
        }

        super.render(guiGraphics, mouseX, mouseY, tickDelta);
    }

    @Override
    public boolean isRelativeContent() {
        return false;
    }
}
