package aiven.zomboidhealthsystem.foundation.gui.widget;

import aiven.zomboidhealthsystem.ZomboidHealthSystem;
import aiven.zomboidhealthsystem.foundation.gui.screen.UI;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

public class UIHealthTabButton extends UITabButton {
    public UIHealthTabButton(int x, int y, boolean isEnabled, UI ui) {
        super(x, y, isEnabled, ui);
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderWidget(context, mouseX, mouseY, delta);
        context.drawTexture(new Identifier(ZomboidHealthSystem.ID,"textures/text/health.png"),
                getX() + 2, getY() + 2, 0,0,20,12,20,12
        );
    }
}
