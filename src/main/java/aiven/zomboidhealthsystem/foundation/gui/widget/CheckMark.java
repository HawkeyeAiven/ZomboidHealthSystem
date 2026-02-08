package aiven.zomboidhealthsystem.foundation.gui.widget;


import aiven.zomboidhealthsystem.ZomboidHealthSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

public class CheckMark extends ModButton {
    private boolean enabled = false;

    public CheckMark(int x, int y, int size) {
        super(x, y, size, size, null, null);
        this.task = (x1,y1,button) -> {
            this.enabled = !enabled;
        };
    }

    @Override
    protected void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderButton(context, mouseX, mouseY, delta);
        if(enabled){
            context.drawTexture(
                    new Identifier(ZomboidHealthSystem.ID,"textures/gui/checkmark.png"),
                    this.getX(), this.getY(), 0, 0, this.getWidth(), this.getHeight(), this.getWidth(), this.getHeight()
            );
        }
    }

    @Override
    protected Identifier getTextureOff() {
        return new Identifier(ZomboidHealthSystem.ID, "textures/gui/square_button_off.png");
    }

    @Override
    protected Identifier getTextureOn() {
        return new Identifier(ZomboidHealthSystem.ID, "textures/gui/square_button_on.png");
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
