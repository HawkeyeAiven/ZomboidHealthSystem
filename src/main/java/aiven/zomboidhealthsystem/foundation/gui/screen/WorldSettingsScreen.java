package aiven.zomboidhealthsystem.foundation.gui.screen;

import aiven.zomboidhealthsystem.foundation.gui.widget.BackButton;
import aiven.zomboidhealthsystem.foundation.gui.widget.CheckMark;
import aiven.zomboidhealthsystem.foundation.gui.widget.ModClickableWidget;
import aiven.zomboidhealthsystem.foundation.gui.widget.ModTextFieldWidget;
import aiven.zomboidhealthsystem.foundation.world.ModServer;
import aiven.zomboidhealthsystem.foundation.world.WorldSettings;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class WorldSettingsScreen extends AbstractModScreen {
    private final Screen createWorldScreen;
    public TextFieldWidget startDay = addClickableWidget(new ModTextFieldWidget(MinecraftClient.getInstance().textRenderer,  115,50,80,20, null));
    public TextFieldWidget daysInSeason = addClickableWidget(new ModTextFieldWidget(MinecraftClient.getInstance().textRenderer,  115,80,80,20, null));
    public TextFieldWidget dayLength = addClickableWidget(new ModTextFieldWidget(MinecraftClient.getInstance().textRenderer,  115,110,80,20, null));
    public CheckMark temperature = addClickableWidget(new CheckMark(115,140,20));
    public CheckMark wind = addClickableWidget(new CheckMark(115,170,20));
    public ClickableWidget backButton = addClickableWidget(new BackButton(this));


    public WorldSettingsScreen(Screen createWorldScreen){
        this.createWorldScreen = createWorldScreen;
        this.startDay.setText(String.valueOf(ModServer.WORLD_SETTINGS_ON_CREATING_WORLD.getStartTicks() / 24000));
        this.daysInSeason.setText(String.valueOf(ModServer.WORLD_SETTINGS_ON_CREATING_WORLD.getDaysInSeason()));
        this.dayLength.setText(String.valueOf(ModServer.WORLD_SETTINGS_ON_CREATING_WORLD.getDayLengthMultiplier()));
        this.temperature.setEnabled(ModServer.WORLD_SETTINGS_ON_CREATING_WORLD.hasTemperature());
        this.wind.setEnabled(ModServer.WORLD_SETTINGS_ON_CREATING_WORLD.hasWind());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float tickDelta) {
        renderBackground(context);
        context.drawCenteredTextWithShadow(
                MinecraftClient.getInstance().textRenderer, Text.of("Start day"),
                50,56,0xFFffffff
        );
        context.drawCenteredTextWithShadow(
                MinecraftClient.getInstance().textRenderer, Text.of("Days in season"),
                50,86,0xFFffffff
        );
        context.drawCenteredTextWithShadow(
                MinecraftClient.getInstance().textRenderer, Text.of("Day length"),
                50,116,0xFFffffff
        );
        context.drawCenteredTextWithShadow(
                MinecraftClient.getInstance().textRenderer, Text.of("Temperature"),
                50,146,0xFFffffff
        );
        context.drawCenteredTextWithShadow(
                MinecraftClient.getInstance().textRenderer, Text.of("Wind"),
                50,176,0xFFffffff
        );
        super.render(context, mouseX, mouseY, tickDelta);
    }

    @Override
    public void close() {
        MinecraftClient.getInstance().setScreen(createWorldScreen);
        try {
            ModServer.WORLD_SETTINGS_ON_CREATING_WORLD = new WorldSettings(
                    Integer.parseInt(dayLength.getText()),
                    Integer.parseInt(daysInSeason.getText()),
                    Integer.parseInt(startDay.getText()) * 24000,
                    temperature.isEnabled(),
                    wind.isEnabled()
                    );
        } catch (NumberFormatException ignored) {
        }
    }
}