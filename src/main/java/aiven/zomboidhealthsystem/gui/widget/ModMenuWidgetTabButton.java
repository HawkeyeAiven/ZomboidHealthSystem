package aiven.zomboidhealthsystem.gui.widget;

import aiven.zomboidhealthsystem.ZomboidHealthSystem;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class ModMenuWidgetTabButton extends AbstractModWidget {

    protected final ModMenuWidget modMenuWidget;
    public boolean isSelected;

    public ModMenuWidgetTabButton(int x, int y, ModMenuWidget modMenuWidget) {
        super(x, y, 24, 10, Component.empty());
        this.modMenuWidget = modMenuWidget;
    }

    @Override
    protected void renderWidget(GuiGraphics context, int x, int y, float tickDelta) {
        if(isSelected) {
            context.blit(ZomboidHealthSystem.getResourceLocation("textures/gui/tab_on.png"),
                    getX(), getY(), 0,0,getWidth(),getHeight(),getWidth(),getHeight()
            );
        } else {
            context.blit(ZomboidHealthSystem.getResourceLocation("textures/gui/tab_off.png"),
                    getX(), getY(), 0,0,getWidth(),getHeight(),getWidth(),getHeight()
            );
        }
    }

    @Override
    public boolean isRelativeContent() {
        return false;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        setSelected(true);
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
