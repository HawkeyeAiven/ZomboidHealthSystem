package aiven.zomboidhealthsystem.gui.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class AbstractModScreen extends Screen {
    protected AbstractModScreen(Component component) {
        super(component);
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float tickDelta) {
        for(GuiEventListener guiEventListener : children()) {
            if(guiEventListener instanceof Renderable renderable) {
                renderable.render(context, mouseX, mouseY, tickDelta);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean bl = false;
        for (GuiEventListener guiEventListener : this.children()) {
            if (guiEventListener.mouseClicked(mouseX, mouseY, button)) {
                this.setFocused(guiEventListener);
                if (button == 0) {
                    this.setDragging(true);
                }
                bl = true;
            }
        }
        return bl;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        boolean bl = false;
        for(GuiEventListener guiEventListener : this.children()) {
            bl = guiEventListener.mouseReleased(mouseX, mouseY, button) || bl;
        }
        return bl;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        boolean bl = false;
        for(GuiEventListener guiEventListener : this.children()) {
            bl = guiEventListener.mouseDragged(mouseX, mouseY, button, deltaX, deltaY) || bl;
        }
        return bl;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        boolean bl = false;
        for(GuiEventListener guiEventListener : this.children()) {
            bl = guiEventListener.mouseScrolled(mouseX, mouseY, delta) || bl;
        }
        return bl;
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        for(GuiEventListener guiEventListener : children()) {
            guiEventListener.mouseMoved(mouseX, mouseY);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public  <T extends GuiEventListener & Renderable & NarratableEntry> @NotNull T addRenderableWidget(T guiEventListener) {
        return super.addRenderableWidget(guiEventListener);
    }

    @Override
    public  <T extends GuiEventListener & NarratableEntry> @NotNull T addWidget(T guiEventListener) {
        return super.addWidget(guiEventListener);
    }

    @Override
    protected  <T extends Renderable> @NotNull T addRenderableOnly(T renderable) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeWidget(GuiEventListener guiEventListener) {
        super.removeWidget(guiEventListener);
    }

    /**
     * Сделано для потокобезопасности
     */
    @Override
    public @NotNull List<? extends GuiEventListener> children() {
        return List.copyOf(super.children());
    }
}
