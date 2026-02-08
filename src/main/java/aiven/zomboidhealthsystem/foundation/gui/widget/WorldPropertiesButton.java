package aiven.zomboidhealthsystem.foundation.gui.widget;

import aiven.zomboidhealthsystem.ZomboidHealthSystem;
import aiven.zomboidhealthsystem.ZomboidHealthSystemClient;
import aiven.zomboidhealthsystem.foundation.gui.screen.WorldSettingsScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

public class WorldPropertiesButton extends ModButton {
    public WorldPropertiesButton(int x, int y) {
        super(x, y, 20,20, null, ((x1, y1, button) -> MinecraftClient.getInstance().setScreen(new WorldSettingsScreen(MinecraftClient.getInstance().currentScreen))));
    }

    @Override
    protected void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderButton(context, mouseX, mouseY, delta);
        context.drawTexture(
                new Identifier(ZomboidHealthSystem.ID,"icon.png"),
                this.getX(),this.getY(),0,0,getWidth(),getHeight(),getWidth(),getHeight()
        );
    }

    @Override
    protected Identifier getTextureOn() {
        return new Identifier(ZomboidHealthSystem.ID,"textures/gui/square_button_on.png");
    }

    @Override
    protected Identifier getTextureOff() {
        return new Identifier(ZomboidHealthSystem.ID,"textures/gui/square_button_off.png");
    }
}
