package aiven.zomboidhealthsystem.gui.widget;

import aiven.zomboidhealthsystem.Config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public class HudWidgetGroup extends ModWidgetGroup {
    private final Minecraft minecraft;

    private final TimeWidget timeWidget;
    private final MoodlesWidget moodlesWidget;
    private final BodyWidget bodyWidget;

    public HudWidgetGroup(Minecraft minecraft) {
        this.minecraft = minecraft;

        Config config = Config.getInstance();

        this.timeWidget = new TimeWidget(config.time_hud_xpos, config.time_hud_ypos, minecraft);
        this.moodlesWidget = new MoodlesWidget(0, config.moodles_hud_ypos);
        this.bodyWidget = new BodyWidget(config.health_hud_xpos, config.health_hud_ypos);

        if(config.time_hud) {
            addChildren(timeWidget);
        }
        if(config.moodles_hud) {
            addChildren(moodlesWidget);
        }
        if(config.health_hud) {
            addChildren(bodyWidget);
        }
    }

    @Override
    protected void renderWidget(GuiGraphics context, int mouseX, int mouseY, float tickDelta) {
        moodlesWidget.setX(minecraft.getWindow().getGuiScaledWidth() - moodlesWidget.getWidth());
        super.renderWidget(context, mouseX, mouseY, tickDelta);
    }

    public void render(GuiGraphics context, float tickDelta) {
        render(context, (int) minecraft.mouseHandler.xpos(), (int) minecraft.mouseHandler.ypos(), tickDelta);
    }

    public TimeWidget getTimeWidget() {
        return timeWidget;
    }

    public MoodlesWidget getMoodlesWidget() {
        return moodlesWidget;
    }

    public BodyWidget getBodyWidget() {
        return bodyWidget;
    }
}
