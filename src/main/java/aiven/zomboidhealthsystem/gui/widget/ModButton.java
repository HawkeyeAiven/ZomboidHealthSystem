package aiven.zomboidhealthsystem.gui.widget;

import aiven.zomboidhealthsystem.utility.StringHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class ModButton extends AbstractModClickableWidget {

    public ModButton(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
    }

    @Override
    protected void renderContent(GuiGraphics context, int mouseX, int mouseY, float tickDelta) {
        context.drawCenteredString(
                Minecraft.getInstance().font,
                cut(getMessage().getString()),
                getX() + (getWidth() / 2),
                getY() + (getHeight() / 2) - 4,
                getMessage().getStyle().getColor() == null ? 0xFFFFFF : getMessage().getStyle().getColor().getValue()
        );
    }

    private String cut(String string) {
        if(!check(string)) {
            int i = string.length() - 1;
            while (true) {
                String str = StringHelper.setMaxLength(string, i, "...");
                if(check(str)) {
                    return str;
                }
                i--;
            }
        } else {
            return string;
        }
    }

    private boolean check(String string) {
        return Minecraft.getInstance().font.width(string) < getWidth() - 4;
    }
}
