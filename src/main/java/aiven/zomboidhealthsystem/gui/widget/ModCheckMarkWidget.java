package aiven.zomboidhealthsystem.gui.widget;

import aiven.zomboidhealthsystem.ZomboidHealthSystem;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ModCheckMarkWidget extends AbstractModClickableWidget {
    private boolean enabled = false;

    public ModCheckMarkWidget(int x, int y, int size) {
        super(x, y, size, size, Component.empty());
    }

    @Override
    protected void renderContent(GuiGraphics context, int mouseX, int mouseY, float tickDelta) {
        if(isEnabled()) {
            context.blit(
                    getCheckMarkTexture(),
                    this.getX(), this.getY(),
                    0, 0,
                    this.getWidth(), this.getHeight(),
                    this.getWidth(), this.getHeight()
            );
        }
    }

    @Override
    public void onClick(double d, double e) {
        setEnabled(!isEnabled());
    }

    protected ResourceLocation getCheckMarkTexture() {
        return ZomboidHealthSystem.getResourceLocation("textures/gui/checkmark.png");
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
