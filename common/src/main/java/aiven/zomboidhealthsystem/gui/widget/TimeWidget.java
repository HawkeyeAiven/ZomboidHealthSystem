package aiven.zomboidhealthsystem.gui.widget;

import aiven.zomboidhealthsystem.utility.ModTime;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class TimeWidget extends AbstractMovableWidget {
    private final Minecraft minecraft;

    public TimeWidget(int x, int y, Minecraft minecraft) {
        super(x, y, 0, 0, Component.empty());
        this.minecraft = minecraft;
    }

    @Override
    protected void renderWidget(GuiGraphics context, int mouseX, int mouseY, float tickDelta) {
        String time = getStringTime();
        if(time != null) {
            width = minecraft.font.width(time);
            height = minecraft.font.lineHeight;

            context.drawString(
                    Minecraft.getInstance().font,
                    time,
                    getX(), getY(),
                    0xFFFFFF
            );
        }
    }

    @Override
    public boolean isRelativeContent() {
        return false;
    }

    public String getStringTime() {
        if(minecraft.level != null) {
            return new ModTime(minecraft.level.getDayTime()).toString();
        } else {
            return null;
        }
    }
}
