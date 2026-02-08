package aiven.zomboidhealthsystem.foundation.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;

import java.util.concurrent.CopyOnWriteArrayList;

@Environment(EnvType.CLIENT)
public class ButtonsContainer extends ModButton {
    public boolean isButtonsHidden;
    public int buttonWidth, buttonHeight;

    public CopyOnWriteArrayList<ClickableWidget> buttons = new CopyOnWriteArrayList<>();

    public ButtonsContainer(int x, int y, int width, int height, Text text, boolean isButtonsHidden){
        super(x, y, width, height, text,null);
        this.isButtonsHidden = isButtonsHidden;
        this.buttonWidth = width;
        this.buttonHeight = height;
    }

    public ButtonsContainer(int x, int y, int width, int height, Text text) {
        this(x, y, width, height, text,true);
    }

    public void addButton(ClickableWidget button) {
        this.buttons.add(button);
    }

    public void removeButton(ClickableWidget button){
        this.buttons.remove(button);
    }

    private void updatePoses(){
        this.height = 20;
        if(!isButtonsHidden) {
            for (ClickableWidget button : this.buttons) {
                if (button.visible) {
                    button.setY(this.getY() + this.height);
                    button.setX(this.getX() + 10);
                    this.height += button.getHeight();
                }
            }
        }
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    public void hideButtons(){
        for(ClickableWidget button : this.buttons){
            button.visible = false;
            button.active = false;
        }
        this.isButtonsHidden = true;
    }

    public void unHideButtons(){
        for(ClickableWidget button : this.buttons){
            button.visible = true;
            button.active = true;
        }
        this.isButtonsHidden = false;
    }

    @Override
    protected void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        updatePoses();
        super.renderButton(context, mouseX, mouseY, delta, this.getX(), this.getY(), this.getButtonWidth(), this.getButtonHeight());
        if(!isButtonsHidden) {
            for (ClickableWidget button : buttons) {
                if(button.visible) {
                    button.render(context, mouseX, mouseY, delta);
                }
            }
        }
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        super.mouseMoved(mouseX, mouseY);
        for(ClickableWidget widget : buttons) {
            widget.mouseMoved(mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        for(ClickableWidget widget : buttons) {
            widget.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for(ClickableWidget widget : buttons) {
            widget.mouseReleased(mouseX, mouseY, button);
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        for(ClickableWidget widget : buttons) {
            widget.mouseScrolled(mouseX, mouseY, amount);
        }
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean bl = false;
        if(!isButtonsHidden) {
            for (ClickableWidget widget : buttons) {
                if (widget.mouseClicked(mouseX, mouseY, button)) {
                    bl = true;
                }
            }
        }
        if(clicked(mouseX,mouseY)){
            if(!isButtonsHidden){
                hideButtons();
            } else {
                unHideButtons();
            }
            this.playDownSound(MinecraftClient.getInstance().getSoundManager());
            return true;
        }
        return bl;
    }

    @Override
    protected boolean clicked(double mouseX, double mouseY) {
        return this.active && this.visible && isMouseOver(mouseX, mouseY);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= (double)this.getX() && mouseY >= (double)this.getY() && mouseX < (double)(this.getX() + 60) && mouseY < (double)(this.getY() + 20);
    }

    public int getButtonHeight() {
        return buttonHeight;
    }

    public int getButtonWidth() {
        return buttonWidth;
    }

    public void setButtonHeight(int buttonHeight) {
        this.buttonHeight = buttonHeight;
    }

    public void setButtonWidth(int buttonWidth) {
        this.buttonWidth = buttonWidth;
    }
}