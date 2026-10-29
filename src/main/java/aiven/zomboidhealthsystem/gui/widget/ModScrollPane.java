package aiven.zomboidhealthsystem.gui.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.concurrent.atomic.AtomicReference;

public class ModScrollPane extends AbstractModWidget {

    private final AtomicReference<AbstractModWidget> content = new AtomicReference<>();
    private double scrollAmount = 0;

    public ModScrollPane(int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty());
    }

    @Override
    public boolean isRelativeContent() {
        return true;
    }

    @Override
    public void renderWidget(GuiGraphics context, int mouseX, int mouseY, float tickDelta) {
        if (this.visible) {
            this.renderBackground(context, mouseX, mouseY, tickDelta);
            if(getContent() != null && getContent().visible) {
                context.enableScissor(getScreenX(), getScreenY(), getScreenX() + getWidth(), getScreenY() + getHeight());
                context.pose().pushPose();
                context.pose().translate(getX(), getY() - this.scrollAmount, 0.0);
                this.renderContent(context, mouseX - getX(), mouseY - getY() + (int) scrollAmount, tickDelta);
                context.pose().popPose();
                context.disableScissor();
            }
        }
    }

    @Override
    public int getContentOffsetY() {
        return super.getContentOffsetY() - (int) scrollAmount;
    }

    protected void renderBackground(GuiGraphics context, int mouseX, int mouseY, float tickDelta) {

    }

    protected void renderContent(GuiGraphics context, int mouseX, int mouseY, float tickDelta) {
        getContent().render(context, mouseX, mouseY, tickDelta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(visible && active) {
            if (isMouseOver(mouseX, mouseY) && getContent() != null && getContent().mouseClicked(mouseX - getX(), mouseY - getY() + scrollAmount, button)) {
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if(visible && active) {
            if (isMouseOver(mouseX, mouseY) && getContent() != null && getContent().mouseReleased(mouseX - getX(), mouseY - getY() + scrollAmount, button)) {
                return true;
            }
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if(visible && active) {
            if (isMouseOver(mouseX, mouseY) && getContent() != null && getContent().mouseDragged(mouseX - getX(), mouseY - getY() + scrollAmount, button, deltaX, deltaY)) {
                return true;
            }
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if(visible && active) {
            if (isMouseOver(mouseX, mouseY) && getContent() != null && getContent().mouseScrolled(mouseX - getX(), mouseY - getY() + scrollAmount, delta)) {
                return true;
            }
            if(isMouseOver(mouseX, mouseY)) {
                setScrollAmount(scrollAmount + (-delta * scrollRate()));
            }
        }
        return false;
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        if(visible && active) {
            if (isMouseOver(mouseX, mouseY) && getContent() != null) {
                getContent().mouseMoved(mouseX - getX(), mouseY - getY() + scrollAmount);
            }
        }
        super.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        if(visible && active) {
            if (getContent() != null && getContent().keyPressed(i, j, k)) {
                return true;
            }
        }
        return super.keyPressed(i, j, k);
    }

    @Override
    public boolean keyReleased(int i, int j, int k) {
        if(visible && active) {
            if (getContent() != null && getContent().keyReleased(i, j, k)) {
                return true;
            }
        }
        return super.keyReleased(i, j, k);
    }

    @Override
    protected boolean isValidClickButton(int i) {
        return false;
    }

    public double scrollRate() {
        return 5.0;
    }

    public double maxScrollAmount() {
        if(getContent() == null || (getContent().getY() + getContent().getHeight() <= getHeight())) {
            return 0;
        }
        return (getContent().getHeight() + getContent().getY()) - getHeight();
    }

    public double minScrollAmount() {
        return 0;
    }

    public double getScrollAmount() {
        return scrollAmount;
    }

    public void setScrollAmount(double scrollAmount) {
        this.scrollAmount = Math.min(Math.max(minScrollAmount(), scrollAmount), maxScrollAmount());
    }

    public  void setContent(AbstractModWidget widget) {
        if(getContent() != null) {
            getContent().setParent(null);
        }
        if(widget != null) {
            widget.setParent(this);
        }
        scrollAmount = 0;
        content.set(widget);
    }

    public AbstractModWidget getContent() {
        return content.get();
    }
}
