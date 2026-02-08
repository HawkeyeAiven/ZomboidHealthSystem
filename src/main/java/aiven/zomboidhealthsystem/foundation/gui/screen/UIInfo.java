package aiven.zomboidhealthsystem.foundation.gui.screen;

import aiven.zomboidhealthsystem.Config;
import aiven.zomboidhealthsystem.ZomboidHealthSystem;
import aiven.zomboidhealthsystem.ZomboidHealthSystemClient;
import aiven.zomboidhealthsystem.foundation.client.ClientWorldInfo;
import aiven.zomboidhealthsystem.foundation.gui.hud.BodyPartHud;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.joml.Vector2f;

@Environment(EnvType.CLIENT)
public non-sealed class UIInfo extends UI {

    public UIInfo(Vector2f ui) {
        super(ui);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float tickDelta) {
        renderBackground(context);

        context.drawTexture(new Identifier(ZomboidHealthSystem.ID,"textures/gui/on_tab.png"),
                (int) pos.x,
                (int) pos.y + 8,
                0,0,24,10,24,10
        );

        context.drawTexture(new Identifier(ZomboidHealthSystem.ID,"textures/gui/tab.png"),
                (int) pos.x + 24,
                (int) pos.y + 8,
                0,0,24,10,24,10
        );

        context.drawTexture(new Identifier(ZomboidHealthSystem.ID,"textures/text/info.png"),
                (int) pos.x + 5,
                (int) pos.y + 9,
                0,0,20,12,20,12
        );

        context.drawTexture(new Identifier(ZomboidHealthSystem.ID,"textures/text/health.png"),
                (int) pos.x + 26,
                (int) pos.y + 9,
                0,0,20,12,20,12
        );

        ClientWorldInfo worldInfo = ZomboidHealthSystemClient.WORLD_INFO;

        if(worldInfo.getDayLengthMultiplier() > 0) {
            context.drawText(
                    MinecraftClient.getInstance().textRenderer,
                    Text.of("Day: " + ((worldInfo.getTicksFromStart() / 24000 / worldInfo.getDayLengthMultiplier()) + 1)),
                    (int) pos.x + 10, (int) pos.y + 25, 0xFFffffff, true
            );
        } else {
            context.drawText(
                    MinecraftClient.getInstance().textRenderer,
                    Text.of("Day: 1"),
                    (int) pos.x + 10, (int) pos.y + 25, 0xFFffffff, true
            );
        }

        context.drawText(
                MinecraftClient.getInstance().textRenderer,
                Text.of( "Time: " + worldInfo.getHours() + (worldInfo.getMinutes() >= 10 ? ":" : ":0") + worldInfo.getMinutes()),
                (int) pos.x + 10, (int) pos.y + 35, 0xFFffffff, true
        );

        context.drawText(
                MinecraftClient.getInstance().textRenderer,
                Text.of("Temperature: " + worldInfo.getTemperature() + "°C"),
                (int) pos.x + 10, (int) pos.y + 45, 0xFFffffff,true
        );

        context.drawText(
                MinecraftClient.getInstance().textRenderer,
                Text.of("World temperature: " + worldInfo.getWorldTemp() + "°C"),
                (int) pos.x + 10, (int) pos.y + 55, 0xFFffffff,true
        );

        context.drawText(
                MinecraftClient.getInstance().textRenderer,
                Text.of("Season temperature: " + worldInfo.getSeasonTemp() + "°C"),
                (int) pos.x + 10, (int) pos.y + 65, 0xFFffffff,true
        );

        context.drawText(
                MinecraftClient.getInstance().textRenderer,
                Text.of("Wind: " + worldInfo.getWind() + "m/s"),
                (int) pos.x + 10, (int) pos.y + 75, 0xFFffffff,true
        );

        BodyPartHud.renderAllParts(context, tickDelta);


        super.render(context, mouseX, mouseY, tickDelta);
    }

    @Override
    public void close() {
        super.close();
        Config.HEALTH_HUD_POS.setValue(BodyPartHud.getPos());
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(mouseX > pos.x + 24 && mouseX < pos.x + 24 + 24 && mouseY > pos.y + 8 && mouseY < pos.y + 8 + 8) {
            client.setScreen(new UIHealth(pos));
            client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if(mouseX - deltaX > BodyPartHud.getPos().x && mouseX - deltaX < BodyPartHud.getPos().x + BodyPartHud.getWidth() && mouseY - deltaY > BodyPartHud.getPos().y && mouseY < BodyPartHud.getPos().y + BodyPartHud.getHeight()) {
            BodyPartHud.POS.x += deltaX;
            BodyPartHud.POS.y += deltaY;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }
}
