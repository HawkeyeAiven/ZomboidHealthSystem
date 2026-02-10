package aiven.zomboidhealthsystem.foundation.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class TemporalButtonsContainer extends ButtonsContainer {
    public TemporalButtonsContainer(int x, int y, int width, int height, Text text) {
        super(x, y, width, height, text, false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(!super.mouseClicked(mouseX, mouseY, button)) {
            destroy();
            return false;
        } else {
            return true;
        }
    }
}
