package aiven.zomboidhealthsystem.foundation.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class TemporalModButton extends ModClickableWidget {
    public TemporalModButton(int x, int y, int width, int height, Text message) {
        super(x, y, width, height, message);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(!super.mouseClicked(mouseX, mouseY, button) && this.active && this.visible){
            this.destroy();
            return false;
        } else {
            return this.visible && this.active;
        }
    }
}
