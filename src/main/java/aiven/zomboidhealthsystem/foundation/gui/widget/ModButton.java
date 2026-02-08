package aiven.zomboidhealthsystem.foundation.gui.widget;

import aiven.zomboidhealthsystem.ZomboidHealthSystem;
import aiven.zomboidhealthsystem.foundation.gui.OnClick;
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
public class ModButton extends ClickableWidget {
    OnClick task;
    protected Identifier currentTexture = getTextureOff();

    public ModButton(int x, int y, int width, int height, Text message, OnClick task) {
        super(x, y, width, height, message);
        this.task = task;
    }

    @Override
    protected void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        renderButton(context, mouseX, mouseY, delta, this.getX(), this.getY(),this.getWidth(), this.getHeight());
    }

    protected void renderButton(DrawContext context, int mouseX, int mouseY, float delta, int x, int y, int width, int height) {
        if(isMouseOver(mouseX, mouseY)) {
            currentTexture = getTextureOn();
        } else {
            currentTexture = getTextureOff();
        }
        context.drawTexture(
                currentTexture,
                x, y,0,0, width, height, width, height
        );
        if(this.getMessage() != null) {
            context.drawCenteredTextWithShadow(
                    MinecraftClient.getInstance().textRenderer,
                    Util.reduce(this.getMessage().getString(), width - 6),
                    x + (width / 2), y + 6, 0xFFffffff
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
        if(this.active && this.visible && this.task != null){
            if(this.clicked(mouseX,mouseY) && this.isValidClickButton(button)){
                this.playDownSound(MinecraftClient.getInstance().getSoundManager());
                this.task.onClick((int) mouseX,(int) mouseY, button);
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

    public void setTask(OnClick task) {
        this.task = task;
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
    }


}
