package aiven.zomboidhealthsystem.foundation.gui.widget;

import aiven.zomboidhealthsystem.ZomboidHealthSystemClient;
import aiven.zomboidhealthsystem.foundation.player.Health;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ScrollableWidget;

import java.util.ArrayList;

@Environment(EnvType.CLIENT)
public class ZomboidWidget extends ScrollableWidget {
    private final ArrayList<BodyPartButton> bodyPartButtons = new ArrayList<>();

    public ZomboidWidget(int x, int y) {
        super(x, y, 149, 135, null);
        Health health = ZomboidHealthSystemClient.HEALTH;
        bodyPartButtons.add(new BodyPartButton(0, 0, health.getHead()));
        bodyPartButtons.add(new BodyPartButton(0, 0, health.getBody()));
        bodyPartButtons.add(new BodyPartButton(0, 0, health.getLeftArm()));
        bodyPartButtons.add(new BodyPartButton(0, 0, health.getRightArm()));
        bodyPartButtons.add(new BodyPartButton(0, 0, health.getLeftLeg()));
        bodyPartButtons.add(new BodyPartButton(0, 0, health.getRightLeg()));
        bodyPartButtons.add(new BodyPartButton(0, 0, health.getLeftFoot()));
        bodyPartButtons.add(new BodyPartButton(0, 0, health.getRightFoot()));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float tickDelta) {
        renderOverlay(context);
        renderButton(context, mouseX, mouseY, tickDelta);
    }


    public void tick() {
        for (BodyPartButton bodyPartButton : bodyPartButtons) {
            bodyPartButton.tick();
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        setScrollY(getScrollY() + (amount * -7));
        return true;
    }

    @Override
    protected int getMaxScrollY() {
        int maxScrollY = 0;
        for (BodyPartButton button : bodyPartButtons) {
            if (button.hasText()) {
                int spaceSize = 8;
                maxScrollY += button.getHeight() + spaceSize;
            }
        }
        return maxScrollY;
    }

    @Override
    protected int getContentsHeight() {
        return 300;
    }

    @Override
    protected double getDeltaYPerScroll() {
        return 0.1;
    }

    @Override
    protected void renderContents(DrawContext context, int mouseX, int mouseY, float delta) {
        int y = 15;
        for (BodyPartButton button : bodyPartButtons) {
            if (button.hasText()) {
                button.setPosition(this.getX() + 10, this.getY() + y);
                button.renderButton(context, mouseX, mouseY, delta);
                y += button.getHeight() + 8;
            }
        }
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for(BodyPartButton bodyPartButton : bodyPartButtons) {
            bodyPartButton.mouseReleased(mouseX, mouseY, button);
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        for(BodyPartButton bodyPartButton : bodyPartButtons) {
            bodyPartButton.mouseMoved(mouseX, mouseY);
        }
        super.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for(BodyPartButton bodyPartButton : bodyPartButtons) {
            bodyPartButton.mouseClicked(mouseX, mouseY, button);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        for(BodyPartButton bodyPartButton : bodyPartButtons) {
            bodyPartButton.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {

    }

    @Override
    protected void drawBox(DrawContext context) {

    }

    @Override
    protected void renderOverlay(DrawContext context) {

    }
}