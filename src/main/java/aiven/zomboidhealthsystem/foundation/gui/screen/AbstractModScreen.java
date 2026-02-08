package aiven.zomboidhealthsystem.foundation.gui.screen;

import aiven.zomboidhealthsystem.ZomboidHealthSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.util.concurrent.CopyOnWriteArrayList;

@Environment(EnvType.CLIENT)
public abstract class AbstractModScreen extends Screen {
    public CopyOnWriteArrayList<ClickableWidget> clickable_widgets = new CopyOnWriteArrayList<>();

    public CopyOnWriteArrayList<TextFieldWidget> textField_widgets = new CopyOnWriteArrayList<>();

    public AbstractModScreen() {
        super(Text.of(ZomboidHealthSystem.ID));
    }

    public void addClickableWidget(ClickableWidget clickableWidget){
        if(!clickable_widgets.contains(clickableWidget)) {
            clickable_widgets.add(clickableWidget);
        }
    }

    public void addTextField(TextFieldWidget textField){
        this.addSelectableChild(textField);
        textField_widgets.add(textField);
        textField.setFocused(false);
        textField.setEditable(false);
    }

    public void destroy(ClickableWidget clickableWidget){
        clickable_widgets.remove(clickableWidget);
    }


    @Override
    public void init() {}

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for(ClickableWidget btn : clickable_widgets){
            btn.mouseClicked(mouseX, mouseY, button);
        }
        for(TextFieldWidget btn : textField_widgets){
            if(btn.mouseClicked(mouseX, mouseY, button)) {
                btn.setFocused(true);
                btn.setEditable(true);
                setInitialFocus(btn);
                setFocused(btn);
            } else {
                btn.setFocused(false);
                btn.setEditable(false);
            }
        }
        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        for(ClickableWidget btn : clickable_widgets){
            btn.mouseScrolled(mouseX, mouseY, amount);
        }
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        for(ClickableWidget btn : clickable_widgets){
            btn.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        super.mouseMoved(mouseX, mouseY);
        for(ClickableWidget btn : clickable_widgets) {
            btn.mouseMoved(mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for(ClickableWidget btn : clickable_widgets) {
            btn.mouseReleased(mouseX, mouseY, button);
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float tickDelta) {
        for(ClickableWidget clickableWidget : clickable_widgets){
            clickableWidget.render(context,mouseX,mouseY,tickDelta);
        }
        for(ClickableWidget clickableWidget : textField_widgets){
            clickableWidget.render(context,mouseX,mouseY,tickDelta);
        }
    }

    @Override
    public void tick() {
        super.tick();
        for(TextFieldWidget btn : textField_widgets){
            btn.tick();
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void close() {
        super.close();
    }
}