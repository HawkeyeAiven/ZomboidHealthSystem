package aiven.zomboidhealthsystem.gui.widget;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractModWidget extends AbstractWidget {
    protected AbstractModWidget parent;

    public AbstractModWidget(int x, int y) {
        this(x, y, 0, 0, Component.empty());
    }

    public AbstractModWidget(int x, int y, Component message) {
        this(x, y, 0, 0, message);
    }

    public AbstractModWidget(int x, int y, int width, int height) {
        this(x, y, width, height, Component.empty());
    }

    public AbstractModWidget(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
    }

    public abstract boolean isRelativeContent();

    public int getContentOffsetX() {
        return getX();
    }

    public int getContentOffsetY() {
        return getY();
    }

    public int getScreenX() {
        int x = getX();
        for(AbstractModWidget widget : getParents()) {
            if(widget.isRelativeContent()) {
                x += widget.getContentOffsetX();
            }
        }
        return x;
    }

    public int getScreenY() {
        int y = getY();
        for(AbstractModWidget widget : getParents()) {
            if(widget.isRelativeContent()) {
                y += widget.getContentOffsetY();
            }
        }
        return y;
    }

    public AbstractModWidget getParent() {
        return parent;
    }

    public List<AbstractModWidget> getParents() {
        if(getParent() == null) {
            return List.of();
        }
        ArrayList<AbstractModWidget> parents = new ArrayList<>();

        AbstractModWidget parent = getParent();
        do {
            parents.add(parent);
            parent = parent.getParent();
        } while (parent != null);

        return parents;
    }

    public void setParent(AbstractModWidget parent) {
        this.parent = parent;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if(visible && active && isValidClickButton(button) && clicked(mouseX, mouseY)) {
            this.onRelease(mouseX, mouseY);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (visible && active && this.isValidClickButton(button) && isMouseOver(mouseX, mouseY)) {
            this.onDrag(mouseX, mouseY, deltaX, deltaY);
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}
