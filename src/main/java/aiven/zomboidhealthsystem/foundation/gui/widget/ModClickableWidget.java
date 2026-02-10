package aiven.zomboidhealthsystem.foundation.gui.widget;

import aiven.zomboidhealthsystem.ZomboidHealthSystem;
import aiven.zomboidhealthsystem.foundation.gui.screen.AbstractModScreen;
import aiven.zomboidhealthsystem.foundation.utility.Util;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ModClickableWidget extends ClickableWidget {
    protected Identifier currentTexture = getTextureOff();
    protected int contentX, contentY, contentWidth, contentHeight;

    public ModClickableWidget(int x, int y, int width, int height, Text message) {
        super(x, y, width, height, message);
        this.contentX = x;
        this.contentY = y;
        this.contentWidth = width;
        this.contentHeight = height;
    }

    @Override
    protected void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        if(isMouseOver(mouseX, mouseY)) {
            currentTexture = getTextureOn();
        } else {
            currentTexture = getTextureOff();
        }
        context.drawTexture(
                currentTexture,
                this.getX(), this.getY(),0,0, this.getWidth(), this.getHeight(), this.getWidth(), this.getHeight()
        );
        if(this.getMessage() != null) {
            context.drawCenteredTextWithShadow(
                    MinecraftClient.getInstance().textRenderer,
                    Util.reduce(this.getMessage().getString(), width - 6),
                    this.getX() + (width / 2), this.getY() + 6, 0xFFffffff
            );
        }
    }

    protected Identifier getTextureOn() {
        return new Identifier(ZomboidHealthSystem.ID, "textures/gui/button.png");
    }

    protected Identifier getTextureOff() {
        return new Identifier(ZomboidHealthSystem.ID, "textures/gui/button_off.png");
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        super.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(this.active && this.visible){
            if(this.clicked(mouseX,mouseY) && this.isValidClickButton(button)){
                this.playDownSound(MinecraftClient.getInstance().getSoundManager());
                this.onClick(mouseX, mouseY);
                return true;
            }
        }
        return false;
    }

    public void destroy(){
        if(MinecraftClient.getInstance().currentScreen instanceof AbstractModScreen screen){
            screen.destroy(this);
            this.visible = false;
            this.active = false;
        }
    }

    public int getLowestPoint() {
        return Math.max(getY() + getHeight(), getContentY() + getContentHeight());
    }

    public int getHighestPoint() {
        return Math.min(getY(), getContentY());
    }

    public int getContentX() {
        return contentX;
    }

    public int getContentY() {
        return contentY;
    }

    public int getContentWidth() {
        return contentWidth;
    }

    public int getContentHeight() {
        return contentHeight;
    }

    @Override
    public void setX(int x) {
        this.contentX = x;
        super.setX(x);
    }

    @Override
    public void setY(int y) {
        this.contentY = y;
        super.setY(y);
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
    }


}
