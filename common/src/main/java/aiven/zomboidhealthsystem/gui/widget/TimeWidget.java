package aiven.zomboidhealthsystem.gui.widget;

import aiven.zomboidhealthsystem.Config;
import aiven.zomboidhealthsystem.utility.Util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.time.format.DateTimeFormatter;

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
            return Util.fromMinecraftDayTime(Minecraft.getInstance().level.getDayTime()).format(DateTimeFormatter.ofPattern(Config.getInstance().time_format));
        } else {
            return null;
        }
    }
}
