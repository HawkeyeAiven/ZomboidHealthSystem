package aiven.zomboidhealthsystem.gui.widget;

import aiven.zomboidhealthsystem.utility.Util;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class ModButtonWithDelay extends ModButton {
    protected Component message;
    private final float delay;
    private boolean isClicked = false;
    private float d = 0;

    public ModButtonWithDelay(int x, int y, int width, int height, Component message, float tickDelay) {
        super(x, y, width, height, message);
        this.message = message;
        this.delay = tickDelay;
    }

    @Override
    protected void renderContent(GuiGraphics context, int mouseX, int mouseY, float tickDelta) {
        if(isClicked && isMouseOver(mouseX, mouseY)) {
            d += tickDelta;
            if(d >= delay) {
                d = 0;
                isClicked = false;
                execute(mouseX, mouseY);
            } else {
                setMessage(Component.literal(String.valueOf(Util.floor((delay - d) / 20, 10))));
            }
        } else {
            d = 0;
            isClicked = false;
            setMessage(message);
        }
        super.renderContent(context, mouseX, mouseY, tickDelta);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        isClicked = true;
    }

    @Override
    public boolean mouseReleased(double d, double e, int i) {
        isClicked = false;
        return super.mouseReleased(d, e, i);
    }

    public void execute(double mouseX, double mouseY) {

    }
}
