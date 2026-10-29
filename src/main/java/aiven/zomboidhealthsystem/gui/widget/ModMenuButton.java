package aiven.zomboidhealthsystem.gui.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.List;

public class ModMenuButton extends ModContainer {
    private static final int offset = 20;

    protected boolean show = false;

    private final ModButton menuButton;

    public ModMenuButton(int x, int y, int width, int height, Component message) {
        super(x, y, width, height);
        menuButton = new ModButton(0, 0, width, height, message) {
            @Override
            public void onClick(double d, double e) {
                setShow(!show);
            }
        };
        addChildren(menuButton);
    }

    @Override
    protected void renderContent(GuiGraphics context, int mouseX, int mouseY, float tickDelta) {
        update();

        menuButton.render(context, mouseX, mouseY, tickDelta);
        if(show) {
            for (AbstractModWidget widget : getChildrenWithoutMenuButton()) {
                widget.render(context, mouseX, mouseY , tickDelta);
            }
        }
    }

    @Override
    protected boolean enableScissor() {
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        mouseX -= getX();
        mouseY -= getY();

        if(menuButton.mouseReleased(mouseX, mouseY, button)) {
            return true;
        }

        if(!visible || !active || !show) {
            return false;
        }

        boolean bl = false;
        for(AbstractModWidget widget : getChildrenWithoutMenuButton()) {
            bl = widget.mouseReleased(mouseX, mouseY, button) || bl;
        }
        return bl;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        mouseX -= getX();
        mouseY -= getY();

        if(menuButton.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }

        if(!visible || !active || !show) {
            return false;
        }

        boolean bl = false;
        for(AbstractModWidget widget : getChildrenWithoutMenuButton()) {
            bl = widget.mouseClicked(mouseX, mouseY, button) || bl;
        }

        return bl;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        mouseX -= getX();
        mouseY -= getY();

        if(menuButton.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
            return true;
        }

        if(!visible || !active || !show) {
            return false;
        }

        boolean bl = false;
        for(AbstractModWidget widget : getChildrenWithoutMenuButton()) {
            bl = widget.mouseDragged(mouseX, mouseY, button, deltaX, deltaY) || bl;
        }
        return bl;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        mouseX -= getX();
        mouseY -= getY();

        if(menuButton.mouseScrolled(mouseX, mouseY, delta)) {
            return true;
        }

        if(!visible || !active || !show) {
            return false;
        }

        boolean bl = false;
        for(AbstractModWidget widget : getChildrenWithoutMenuButton()) {
            bl = widget.mouseScrolled(mouseX, mouseY, delta) || bl;
        }
        return bl;
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        mouseX -= getX();
        mouseY -= getY();

        menuButton.mouseMoved(mouseX, mouseY);

        if(!visible || !active || !show) {
            return;
        }

        for(AbstractModWidget widget : getChildrenWithoutMenuButton()) {
            widget.mouseMoved(mouseX, mouseY);
        }
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        if(!visible || !active || !show) {
            return false;
        }

        if(menuButton.keyPressed(i, j, k)) {
            return true;
        }

        boolean bl = false;
        for(AbstractModWidget widget : getChildrenWithoutMenuButton()) {
            bl = widget.keyPressed(i, j, k) || bl;
        }
        return bl;
    }

    @Override
    public boolean keyReleased(int i, int j, int k) {
        if(!visible || !active || !show) {
            return false;
        }

        if(menuButton.keyReleased(i, j, k)) {
            return true;
        }

        boolean bl = false;
        for(AbstractModWidget widget : getChildrenWithoutMenuButton()) {
            bl = widget.keyReleased(i, j, k) || bl;
        }
        return bl;
    }

    public void update() {
        if (!show) {
            this.width = menuButton.getWidth();
            this.height = menuButton.getHeight();
            return;
        }

        {
            int widgetWidth = menuButton.getWidth();
            for (AbstractModWidget menuItem : getChildrenWithoutMenuButton()) {
                if (menuItem.visible) {
                    menuItem.setX(offset);
                    if(menuItem instanceof ModMenuButton modMenuButton) {
                        widgetWidth = Math.max(widgetWidth, modMenuButton.getMenuButton().getWidth());
                    } else {
                        widgetWidth = Math.max(widgetWidth, menuItem.getWidth());
                    }
                }
            }

            for(AbstractModWidget menuItem : getChildrenWithoutMenuButton()) {
                if(menuItem instanceof ModMenuButton modMenuButton) {
                    modMenuButton.getMenuButton().setWidth(widgetWidth);
                } else {
                    menuItem.setWidth(widgetWidth);
                }
            }

            for(AbstractModWidget menuItem : getChildrenWithoutMenuButton()) {
                if(menuItem instanceof ModMenuButton modMenuButton) {
                    modMenuButton.update();
                }
            }

            int width = 0;
            for(AbstractModWidget menuItem : getChildrenWithoutMenuButton()) {
                width += menuItem.getWidth();
            }

            this.width = width;

        }

        {
            int y = menuButton.getHeight();
            int height = menuButton.getHeight();
            for (AbstractModWidget menuItem : getChildrenWithoutMenuButton()) {
                if (menuItem.visible) {
                    menuItem.setY(y);
                    y += menuItem.getHeight();
                    height += menuItem.getHeight();
                }
            }
            this.height = height;
        }
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    public boolean isShow() {
        return show;
    }

    public ModButton getMenuButton() {
        return menuButton;
    }

    public List<AbstractModWidget> getChildrenWithoutMenuButton() {
        return List.copyOf(getChildren()).stream().filter((abstractModWidget -> abstractModWidget != menuButton)).toList();
    }
}
