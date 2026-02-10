package aiven.zomboidhealthsystem.foundation.gui.widget;

import aiven.zomboidhealthsystem.foundation.utility.Util;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class ModButtonWithDelay extends ModClickableWidget {
    private final float delay;
    private float d = 0;
    private boolean isClicked = false;
    private final Text message;

    public ModButtonWithDelay(int x, int y, int width, int height, float delay, Text message) {
        super(x, y, width, height, message);
        this.delay = delay;
        this.message = message;
    }

    @Override
    protected void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderButton(context, mouseX, mouseY, delta);
        if(isClicked && isMouseOver(mouseX, mouseY)) {
            d += delta;
            if(d >= delay) {
                onClick(mouseX, mouseY);
                d = 0;
                isClicked = false;
            }
            setMessage(Text.of(String.valueOf(Util.floor((delay - d) / 20, 10))));
        } else {
            d = 0;
            isClicked = false;
            setMessage(message);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean bl = isValidClickButton(button) && clicked(mouseX, mouseY) && this.visible && this.active;
        isClicked = bl;
        return bl;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        isClicked = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        this.playDownSound(MinecraftClient.getInstance().getSoundManager());
        super.onClick(mouseX, mouseY);
    }
}
