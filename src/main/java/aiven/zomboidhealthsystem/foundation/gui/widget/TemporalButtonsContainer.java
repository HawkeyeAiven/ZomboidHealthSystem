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
        if(button != 0){
            this.destroy();
            return false;
        } else {
            boolean bl = false;
            if (!isButtonsHidden) {
                for (ClickableWidget btn : buttons) {
                    if (btn.mouseClicked(mouseX, mouseY, button)) {
                        if (!(btn instanceof BandageContainer)) {
                            this.destroy();
                            return true;
                        }
                        bl = true;
                    }
                }
            }
            if (clicked(mouseX, mouseY)) {
                if (!isButtonsHidden) {
                    hideButtons();
                } else {
                    unHideButtons();
                }
                this.playDownSound(MinecraftClient.getInstance().getSoundManager());
                return true;
            } else if (!bl) {
                this.destroy();
            }
            return bl;
        }
    }
}
