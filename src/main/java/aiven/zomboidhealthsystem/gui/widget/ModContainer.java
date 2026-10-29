package aiven.zomboidhealthsystem.gui.widget;

import net.minecraft.client.gui.GuiGraphics;

public class ModContainer extends ModWidgetGroup {

    public ModContainer(int x, int y, int width, int height) {
        super();
        this.setX(x);
        this.setY(y);
        this.width = width;
        this.height = height;
    }

    @Override
    public boolean isRelativeContent() {
        return true;
    }

    @Override
    protected void renderWidget(GuiGraphics context, int mouseX, int mouseY, float tickDelta) {
        int x = getX();
        int y = getY();

        if(enableScissor()) {
            context.enableScissor(getScreenX(), getScreenY(), getScreenX() + getWidth(), getScreenY() + getHeight());
        }

        context.pose().pushPose();
        context.pose().translate(x, y, 0);

        renderContent(context, mouseX - x, mouseY - y, tickDelta);

        context.pose().popPose();

        if(enableScissor()) {
            context.disableScissor();
        }
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        mouseX -= getX();
        mouseY -= getY();

        if(!visible || !active) {
            return false;
        }

        boolean bl = false;
        for(AbstractModWidget widget : getChildren()) {
            bl = widget.mouseReleased(mouseX, mouseY, button) || bl;
        }
        return bl;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        mouseX -= getX();
        mouseY -= getY();

        if(!visible || !active) {
            return false;
        }

        boolean bl = false;
        for(AbstractModWidget widget : getChildren()) {
            bl = widget.mouseClicked(mouseX, mouseY, button) || bl;
        }
        return bl;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        mouseX -= getX();
        mouseY -= getY();

        if(!visible || !active) {
            return false;
        }

        boolean bl = false;
        for(AbstractModWidget widget : getChildren()) {
            bl = widget.mouseDragged(mouseX, mouseY, button, deltaX, deltaY) || bl;
        }
        return bl;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        mouseX -= getX();
        mouseY -= getY();

        if(!visible || !active) {
            return false;
        }

        boolean bl = false;
        for(AbstractModWidget widget : getChildren()) {
            bl = widget.mouseScrolled(mouseX, mouseY, delta) || bl;
        }
        return bl;
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        mouseX -= getX();
        mouseY -= getY();

        if(!visible || !active) {
            return;
        }

        for(AbstractModWidget widget : getChildren()) {
            widget.mouseMoved(mouseX, mouseY);
        }
    }

    protected boolean enableScissor() {
        return true;
    }
}
