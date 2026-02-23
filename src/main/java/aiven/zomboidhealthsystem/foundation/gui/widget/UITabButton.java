package aiven.zomboidhealthsystem.foundation.gui.widget;

import aiven.zomboidhealthsystem.ZomboidHealthSystem;
import aiven.zomboidhealthsystem.foundation.gui.screen.UI;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

public class UITabButton extends ModClickableWidget {

    public boolean isEnabled;
    protected final UI ui;

    public UITabButton(int x, int y, boolean isEnabled, UI ui) {
        super(x, y, 24, 10, null);
        this.isEnabled = isEnabled;
        this.ui = ui;
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderWidget(context, mouseX, mouseY, delta);
        if(isEnabled) {
            context.drawTexture(new Identifier(ZomboidHealthSystem.ID,"textures/gui/tab_on.png"),
                    getX(), getY(), 0,0,getWidth(),getHeight(),getWidth(),getHeight()
            );
        } else {
            context.drawTexture(new Identifier(ZomboidHealthSystem.ID,"textures/gui/tab_off.png"),
                    getX(), getY(), 0,0,getWidth(),getHeight(),getWidth(),getHeight()
            );
        }
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        this.isEnabled = true;
        for(UITabButton tabButton : ui.tabButtons) {
            if(tabButton != this) {
                tabButton.isEnabled = false;
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(this.active && this.visible){
            if(!this.isEnabled && this.clicked(mouseX,mouseY) && this.isValidClickButton(button)){
                this.playDownSound(MinecraftClient.getInstance().getSoundManager());
                this.onClick(mouseX, mouseY);
                return true;
            }
        }
        return false;
    }
}
