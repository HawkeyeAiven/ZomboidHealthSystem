package aiven.zomboidhealthsystem.gui.widget;

import aiven.zomboidhealthsystem.ZomboidHealthSystem;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class HealthTabButton extends ModMenuWidgetTabButton {

    public HealthTabButton(int x, int y, ModMenuWidget modMenuWidget) {
        super(x, y, modMenuWidget);
    }

    @Override
    protected void renderWidget(GuiGraphics context, int x, int y, float tickDelta) {
        super.renderWidget(context, x, y, tickDelta);
        context.blit(ZomboidHealthSystem.getResourceLocation("textures/text/health.png"),
                getX() + 2, getY() + 2, 0,0,20,12,20,12
        );
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        if(selected) {
            modMenuWidget.getInfoTabButton().setSelected(false);
        }
        modMenuWidget.getHealthTabGroup().visible = selected;
    }
}
