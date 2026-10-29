package aiven.zomboidhealthsystem.gui.widget;

import net.minecraft.client.gui.GuiGraphics;

public class HBox extends ModContainer {

    public HBox(int x, int y, int width) {
        super(x, y, width, 0);
    }


    @Override
    protected void renderContent(GuiGraphics context, int mouseX, int mouseY, float tickDelta) {
        update();
        super.renderContent(context, mouseX, mouseY, tickDelta);
    }

    public int getSpacing() {
        return 0;
    }

    public void update() {
        int y = 0;
        for(AbstractModWidget widget : children) {
            if(!widget.visible) {
                continue;
            }
            widget.setY(y);
            y += widget.getHeight() + getSpacing();
        }
        setHeight(Math.max(y - getSpacing(), 0));
    }
}
