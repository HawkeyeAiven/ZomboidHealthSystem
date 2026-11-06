package aiven.zomboidhealthsystem.gui.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ModWidgetGroup extends AbstractModWidget {
    protected final CopyOnWriteArrayList<AbstractModWidget> children = new CopyOnWriteArrayList<>();

    public ModWidgetGroup() {
        super(0, 0, 0, 0, Component.empty());
    }

    @Override
    public boolean isRelativeContent() {
        return false;
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float tickDelta) {
        if(visible) {
            renderWidget(context, mouseX, mouseY, tickDelta);
        }
    }

    protected void renderWidget(GuiGraphics context, int mouseX, int mouseY, float tickDelta) {
        renderContent(context, mouseX, mouseY, tickDelta);
    }

    protected void renderContent(GuiGraphics context, int mouseX, int mouseY, float tickDelta) {
        for(AbstractModWidget widget : getChildren()) {
            widget.render(context, mouseX, mouseY, tickDelta);
        }
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
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
        if(!visible || !active) {
            return;
        }

        for(AbstractModWidget widget : getChildren()) {
            widget.mouseMoved(mouseX, mouseY);
        }
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        if(!visible || !active) {
            return false;
        }

        boolean bl = false;
        for(AbstractModWidget widget : getChildren()) {
            bl = widget.keyPressed(i, j, k) || bl;
        }
        return bl;
    }

    @Override
    public boolean keyReleased(int i, int j, int k) {
        if(!visible || !active) {
            return false;
        }

        boolean bl = false;
        for(AbstractModWidget widget : getChildren()) {
            bl = widget.keyReleased(i, j, k) || bl;
        }
        return bl;
    }

    public void addChildren(AbstractModWidget children) {
        children.setParent(this);
        this.children.add(children);
    }

    public void removeChildren(AbstractModWidget children) {
        children.setParent(null);
        this.children.remove(children);
    }

    public List<AbstractModWidget> getChildren() {
        return List.copyOf(children);
    }
}
