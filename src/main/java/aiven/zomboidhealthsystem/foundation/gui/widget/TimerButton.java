package aiven.zomboidhealthsystem.foundation.gui.widget;


import aiven.zomboidhealthsystem.foundation.utility.Util;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class TimerButton extends ModClickableWidget {
    protected float time;
    protected final float maxTime;
    private final boolean destroyOnEnd, clickOnEnd;
    private final String message;
    private boolean isStopped = false;


    public TimerButton(int x, int y, int width, int height, float time, boolean destroyOnEnd, boolean clickOnEnd, String message) {
        super(x, y, width, height, null);
        this.time = time;
        this.maxTime = time;
        this.destroyOnEnd = destroyOnEnd;
        this.clickOnEnd = clickOnEnd;
        this.message = message;
    }

    @Override
    protected void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderButton(context, mouseX, mouseY, delta);
        if(!isStopped) {
            if (time < 0) {
                setMessage(Text.of(message));
                if (clickOnEnd) {
                    this.onClick(mouseX, mouseY);
                }
                if (destroyOnEnd) {
                    this.destroy();
                }
                isStopped = true;
            } else {
                setMessage(Text.of(String.valueOf(Util.floor((double) time / 20, 10))));
                this.time -= delta;
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(time < 0) {
            return super.mouseClicked(mouseX, mouseY, button);
        } else {
            return false;
        }
    }
}