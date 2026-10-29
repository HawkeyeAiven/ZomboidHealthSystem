package aiven.zomboidhealthsystem.gui.widget;

import aiven.zomboidhealthsystem.ZomboidHealthSystem;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public abstract class AbstractModClickableWidget extends AbstractModWidget {
    protected ResourceLocation currentTexture = getTextureOff();

    public AbstractModClickableWidget(int x, int y) {
        super(x, y);
    }

    public AbstractModClickableWidget(int x, int y, Component message) {
        super(x, y, message);
    }

    public AbstractModClickableWidget(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    public AbstractModClickableWidget(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
    }

    protected abstract void renderContent(GuiGraphics context, int mouseX, int mouseY, float tickDelta);

    @Override
    protected final void renderWidget(GuiGraphics context, int mouseX, int mouseY, float tickDelta) {
        context.pose().pushPose();

        // Это сделано из-за проблемы с порядком отрисовки текста:
        // по какой-то причине текст всегда выходит на первый план.
        // Правда, проблему это решает не полностью, но пойдет.
        context.pose().translate(0, 0, 1);

        renderBackground(context, mouseX, mouseY, tickDelta);
        renderContent(context, mouseX, mouseY, tickDelta);

        context.pose().popPose();
    }

    protected void renderBackground(GuiGraphics context, int mouseX, int mouseY, float tickDelta) {
        if(isMouseOver(mouseX, mouseY) && active) {
            currentTexture = getTextureOn();
        } else {
            currentTexture = getTextureOff();
        }
        context.blit(currentTexture, getX(), getY(), 0, 0, getWidth(), getHeight(), getWidth(), getHeight());
    }

    @Override
    public boolean isRelativeContent() {
        return false;
    }

    protected ResourceLocation getTextureOn() {
        return ZomboidHealthSystem.getResourceLocation("textures/gui/button.png");
    }

    protected ResourceLocation getTextureOff() {
        return ZomboidHealthSystem.getResourceLocation("textures/gui/button_off.png");
    }
}
