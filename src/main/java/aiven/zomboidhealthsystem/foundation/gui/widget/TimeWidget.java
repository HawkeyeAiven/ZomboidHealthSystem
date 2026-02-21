package aiven.zomboidhealthsystem.foundation.gui.widget;

import aiven.zomboidhealthsystem.Config;
import aiven.zomboidhealthsystem.foundation.utility.TimeOfDay;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.joml.Vector2f;

public class TimeWidget extends ModClickableWidget {

    private final Vector2f pos;
    private final MinecraftClient client = MinecraftClient.getInstance();

    public TimeWidget() {
        super((int) Config.TIME_HUD_POS.getValue().x, (int) Config.TIME_HUD_POS.getValue().y, 6,6, null);
        this.pos = Config.TIME_HUD_POS.getValue();
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        String time = getStringTime();
        if(time != null) {
            this.width = client.textRenderer.getWidth(time);
            context.drawText(
                    this.client.textRenderer,
                    Text.of(time),
                    this.getX(), this.getY(), 0xFFffffff, true
            );
        }
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        double lastMouseX = mouseX - deltaX;
        double lastMouseY = mouseY - deltaY;
        if(isMouseOver(lastMouseX, lastMouseY) && button == 0) {
            this.pos.add((float) deltaX, (float) deltaY);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return false;
    }

    public void setPos(Vector2f pos) {
        this.pos.set(pos);
    }

    @Override
    public void setX(int x) {
        this.pos.x = x;
    }

    @Override
    public void setY(int y) {
        this.pos.y = y;
    }

    @Override
    public int getX() {
        return (int) pos.x;
    }

    @Override
    public int getY() {
        return (int) pos.y;
    }

    public String getStringTime() {
        if(client.world != null) {
            return new TimeOfDay(client.world.getTimeOfDay()).getModTime();
        } else {
            return null;
        }
    }
}
