package aiven.zomboidhealthsystem.gui.widget;

import aiven.zomboidhealthsystem.gui.screen.AbstractModScreen;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class TemporalAdvancedMenuButton extends AdvancedModMenuButton {

    public TemporalAdvancedMenuButton(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
    }

    @Override
    protected void execute(Item item) {
        super.execute(item);
        destroy();
    }

    @Override
    protected AdvancedModMenuButton createMenuButton(int width, int height, Component component) {
        return new TemporalAdvancedMenuButton(0, 0, width, height, component);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(!super.mouseClicked(mouseX, mouseY, button)) {
            destroy();
            return false;
        } else {
            return true;
        }
    }

    public void destroy() {
        visible = false;
        if(getParent() != null) {
            if(getParent() instanceof TemporalAdvancedMenuButton menuButton) {
                menuButton.destroy();
                return;
            }
            if(getParent() instanceof ModWidgetGroup modWidgetGroup) {
                modWidgetGroup.removeChildren(this);
            }
        } else {
            if(Minecraft.getInstance().screen instanceof AbstractModScreen modScreen) {
                modScreen.removeWidget(this);
            }
        }
    }
}
