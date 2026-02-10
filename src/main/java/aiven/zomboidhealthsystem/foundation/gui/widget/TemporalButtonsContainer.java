package aiven.zomboidhealthsystem.foundation.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class TemporalButtonsContainer extends ButtonsContainer {
    public TemporalButtonsContainer(int x, int y, int width, int height, Text text) {
        super(x, y, width, height, text, false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(!isValidClickButton(button)){
            this.destroy();
            return false;
        } else {
            if (!areButtonsHidden) {
                for (ClickableWidget btn : buttons) {
                    if (btn.mouseClicked(mouseX, mouseY, button)) {
                        this.destroy();
                        return true;
                    }
                }
            }
            if (clicked(mouseX, mouseY)) {
                if (!areButtonsHidden) {
                    hideButtons();
                } else {
                    unHideButtons();
                }
                this.playDownSound(MinecraftClient.getInstance().getSoundManager());
                return true;
            } else {
                destroy();
                return false;
            }
        }
    }
}
