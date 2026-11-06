package aiven.zomboidhealthsystem.gui.widget;

import aiven.zomboidhealthsystem.ZomboidHealthSystem;

import net.minecraft.client.gui.GuiGraphics;

public class InfoTabButton extends ModMenuWidgetTabButton {
    public InfoTabButton(int x, int y, ModMenuWidget modMenuWidget) {
        super(x, y, modMenuWidget);
    }

    @Override
    protected void renderWidget(GuiGraphics context, int x, int y, float tickDelta) {
        super.renderWidget(context, x, y, tickDelta);
        context.blit(ZomboidHealthSystem.getResourceLocation("textures/text/info.png"),
                getX() + 5, getY() + 2, 0,0,20,12,20,12
        );
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        super.onClick(mouseX, mouseY);
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        if(selected) {
            modMenuWidget.getHealthTabButton().setSelected(false);
        }
        modMenuWidget.getInfoTabGroup().visible = selected;
    }
}
