package aiven.zomboidhealthsystem.gui.widget;

import net.minecraft.network.chat.Component;

public abstract class AbstractMovableWidget extends AbstractModWidget {
    protected float floatX = 0;
    protected float floatY = 0;

    public AbstractMovableWidget(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
        setX(x);
        setY(y);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        double lastMouseX = mouseX - deltaX;
        double lastMouseY = mouseY - deltaY;
        if(canMove(lastMouseX, lastMouseY, button)) {
            if(movableX()) {
                this.setFloatX(getFloatX() + (float) deltaX);
            }
            if(movableY()) {
                this.setFloatY(getFloatY() + (float) deltaY);
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void setX(int i) {
        setFloatX((float) i);
    }

    @Override
    public void setY(int i) {
        setFloatY((float) i);
    }

    protected boolean canMove(double mouseX, double mouseY, int button) {
        return isMouseOver(mouseX, mouseY) && isValidClickButton(button);
    }

    protected boolean movableX() {
        return true;
    }

    protected boolean movableY() {
        return true;
    }

    public float getFloatX() {
        return floatX;
    }

    public void setFloatX(float floatX) {
        this.floatX = floatX;
        super.setX((int) floatX);
    }

    public float getFloatY() {
        return floatY;
    }

    public void setFloatY(float floatY) {
        this.floatY = floatY;
        super.setY((int) floatY);
    }
}
