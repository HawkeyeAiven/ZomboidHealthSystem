package aiven.zomboidhealthsystem.gui.widget;

import aiven.zomboidhealthsystem.gui.screen.ModMenuScreen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;

public abstract class AbstractBodyWidget extends AbstractMovableWidget {

    private final Mode mode;

    public AbstractBodyWidget(int x, int y, int width, int height, Mode mode) {
        super(x, y, width, height, Component.empty());
        this.mode = mode;
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float tickDelta) {
        if(mode.equals(Mode.HUD)) {
            Minecraft minecraft = Minecraft.getInstance();
            LocalPlayer player = minecraft.player;
            Screen screen = minecraft.screen;

            if(screen instanceof ModMenuScreen modMenuScreen) {
                if(modMenuScreen.getModMenuWidget().getHealthTabGroup().visible) {
                    return;
                }
            } else {
                if(player == null || player.isSpectator() || player.isCreative()) {
                    return;
                }
            }
        }
        super.render(context, mouseX, mouseY, tickDelta);
    }

    @Override
    public boolean isRelativeContent() {
        return false;
    }

    @Override
    protected boolean isValidClickButton(int i) {
        if(mode.equals(Mode.WIDGET)) {
            return false;
        }
        return super.isValidClickButton(i);
    }

    public enum Mode {
        HUD,
        WIDGET
    }
}
