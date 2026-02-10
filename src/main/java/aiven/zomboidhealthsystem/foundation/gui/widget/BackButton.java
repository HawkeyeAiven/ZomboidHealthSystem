package aiven.zomboidhealthsystem.foundation.gui.widget;

import aiven.zomboidhealthsystem.foundation.gui.screen.WorldSettingsScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class BackButton extends ModClickableWidget {
    private final WorldSettingsScreen worldSettingsScreen;
    public BackButton(WorldSettingsScreen worldSettingsScreen) {
        super(20, MinecraftClient.getInstance().getWindow().getScaledHeight() - 75,40,20, Text.of("Back"));
        this.worldSettingsScreen = worldSettingsScreen;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        worldSettingsScreen.close();
    }
}
