package aiven.zomboidhealthsystem.foundation.gui.widget;


import aiven.zomboidhealthsystem.foundation.gui.OnClick;
import aiven.zomboidhealthsystem.foundation.utility.Util;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class TimerButton extends ModButton {
    int time;
    final int maxTime;

    public TimerButton(int x, int y, int width, int height, int time, OnClick task) {
        super(x, y, width, height, null, task);
        this.time = time;
        this.maxTime = time;
    }

    @Override
    protected void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderButton(context, mouseX, mouseY, delta);
        setMessage(Text.of(String.valueOf(Util.floor((double) time / 1000,10))));
        if(time < 0){
            this.destroy();
            task.onClick(0,0,0);
        } else {
            this.time -= (int) (1000.0F / (float) MinecraftClient.getInstance().getCurrentFps());
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return false;
    }


}
