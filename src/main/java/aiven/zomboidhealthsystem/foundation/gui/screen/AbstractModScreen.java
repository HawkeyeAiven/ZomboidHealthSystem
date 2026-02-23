package aiven.zomboidhealthsystem.foundation.gui.screen;

import aiven.zomboidhealthsystem.ZomboidHealthSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;

import java.util.concurrent.CopyOnWriteArrayList;

@Environment(EnvType.CLIENT)
public abstract class AbstractModScreen extends Screen {
    public CopyOnWriteArrayList<ClickableWidget> clickable_widgets = new CopyOnWriteArrayList<>();

    public AbstractModScreen() {
        super(Text.of(ZomboidHealthSystem.ID));
    }

    public <T extends ClickableWidget> T addClickableWidget(T clickableWidget){
        if(!clickable_widgets.contains(clickableWidget)) {
            clickable_widgets.add(0, clickableWidget);
        }
        return clickableWidget;
    }

    public void removeClickableWidget(ClickableWidget clickableWidget){
        clickable_widgets.remove(clickableWidget);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for(ClickableWidget btn : clickable_widgets){
            if(btn.visible && btn.active) {
                if(btn.mouseClicked(mouseX, mouseY, button)) {
                    setFocused(btn);
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        for(ClickableWidget btn : clickable_widgets){
            if(btn.visible && btn.active) {
                btn.mouseScrolled(mouseX, mouseY, amount);
            }
        }
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        for(ClickableWidget btn : clickable_widgets){
            if(btn.visible && btn.active) {
                btn.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
            }
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        super.mouseMoved(mouseX, mouseY);
        for(ClickableWidget btn : clickable_widgets) {
            if(btn.visible && btn.active) {
                btn.mouseMoved(mouseX, mouseY);
            }
        }
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for(ClickableWidget btn : clickable_widgets) {
            if(btn.visible && btn.active) {
                btn.mouseReleased(mouseX, mouseY, button);
            }
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float tickDelta) {
        for(int i = clickable_widgets.size() - 1; i >= 0; i--) {
            ClickableWidget btn = clickable_widgets.get(i);
            if(btn.visible) {
                clickable_widgets.get(i).render(context, mouseX, mouseY, tickDelta);
            }
        }
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}