package aiven.zomboidhealthsystem.foundation.gui.widget;

import aiven.zomboidhealthsystem.foundation.utility.Util;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;

import java.util.concurrent.CopyOnWriteArrayList;

@Environment(EnvType.CLIENT)
public class ButtonsContainer extends ModClickableWidget {
    public boolean areButtonsHidden;

    public CopyOnWriteArrayList<ModClickableWidget> buttons = new CopyOnWriteArrayList<>();
    private final int buttonHeight;

    public ButtonsContainer(int x, int y, int width, int height, Text text, boolean areButtonsHidden){
        super(x, y, width, height, text);
        this.areButtonsHidden = areButtonsHidden;
        this.buttonHeight = height;
    }

    public ButtonsContainer(int x, int y, int width, int height, Text text) {
        this(x, y, width, height, text,true);
    }

    public void addButton(ModClickableWidget button) {
        this.buttons.add(button);
    }

    public void removeButton(ModClickableWidget button){
        this.buttons.remove(button);
    }

    private void updatePoses(){
        this.height = buttonHeight;
        if(!areButtonsHidden) {
            for (ModClickableWidget button : this.buttons) {
                if (button.visible) {
                    button.setY(this.getY() + this.getHeight());
                    button.setX(this.getX() + 10);
                    this.height += button.getHeight();
                }
            }
        }
    }

    public void hideButtons(){
        for(ClickableWidget button : this.buttons){
            button.visible = false;
            button.active = false;
        }
        this.areButtonsHidden = true;
    }

    public void unHideButtons(){
        for(ClickableWidget button : this.buttons){
            button.visible = true;
            button.active = true;
        }
        this.areButtonsHidden = false;
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        if(isMouseOver(mouseX, mouseY)) {
            currentTexture = getTextureOn();
        } else {
            currentTexture = getTextureOff();
        }
        context.drawTexture(
                currentTexture,
                this.getX(), this.getY(),0,0, this.getWidth(), this.buttonHeight, this.getWidth(), this.buttonHeight
        );
        if(this.getMessage() != null) {
            context.drawCenteredTextWithShadow(
                    MinecraftClient.getInstance().textRenderer,
                    Util.reduce(this.getMessage().getString(), width - 6),
                    this.getX() + (width / 2), this.getY() + 6, 0xFFffffff
            );
        }
        updatePoses();
        if(!areButtonsHidden) {
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
        if(!areButtonsHidden) {
            for (ClickableWidget widget : buttons) {
                if (widget.mouseClicked(mouseX, mouseY, button)) {
                    bl = true;
                }
            }
        }
        if(clicked(mouseX,mouseY) && isValidClickButton(button)){
            if(!areButtonsHidden){
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
        return this.active && this.visible && mouseX >= (double)this.getX() && mouseY >= (double)this.getY() && mouseX < (double)(this.getX() + this.width) && mouseY < (double)(this.getY() + this.buttonHeight);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return this.active && this.visible && mouseX >= (double)this.getX() && mouseY >= (double)this.getY() && mouseX < (double)(this.getX() + this.width) && mouseY < (double)(this.getY() + this.buttonHeight);
    }
}