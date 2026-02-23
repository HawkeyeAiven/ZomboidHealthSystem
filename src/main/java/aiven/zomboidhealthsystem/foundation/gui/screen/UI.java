package aiven.zomboidhealthsystem.foundation.gui.screen;

import aiven.zomboidhealthsystem.ModKeyBindings;
import aiven.zomboidhealthsystem.ZomboidHealthSystem;
import aiven.zomboidhealthsystem.ZomboidHealthSystemClient;
import aiven.zomboidhealthsystem.foundation.client.ClientWorldInfo;
import aiven.zomboidhealthsystem.foundation.gui.widget.*;
import aiven.zomboidhealthsystem.foundation.utility.TimeOfDay;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.joml.Vector2f;

@Environment(EnvType.CLIENT)
public class UI extends AbstractModScreen {
    private static Vector2f LAST_POS = new Vector2f(
            150 + ((float) (MinecraftClient.getInstance().getWindow().getScaledWidth() - 640) / 3),
            80 + ((float) (MinecraftClient.getInstance().getWindow().getScaledHeight() - 360) / 3));

    protected Vector2f uiPos;
    private final TimeWidget timeWidget;
    private final MoodlesWidget moodlesWidget;
    private final BodyPartListWidget bodyPartListWidget;
    private final BodyPartsWidget bodyPartsWidget;
    private final UIHealthTabButton uiHealthTabButton;
    private final UIInfoTabButton uiInfoTabButton;
    public final UITabButton[] tabButtons;
    private MutableText damageText = Text.translatable("zomboidhealthsystem.health.hud.completely_health");

    public UI() {
        uiPos = LAST_POS;
        this.timeWidget = addClickableWidget(ZomboidHealthSystemClient.HUD.getTimeWidget());
        this.moodlesWidget = addClickableWidget(ZomboidHealthSystemClient.HUD.getMoodlesWidget());
        this.bodyPartListWidget = addClickableWidget(new BodyPartListWidget((int) uiPos.x + 75, (int) uiPos.y + 45));
        this.bodyPartsWidget = addClickableWidget(ZomboidHealthSystemClient.HUD.getBodyPartsWidget());
        this.uiHealthTabButton = addClickableWidget(new UIHealthTabButton((int) uiPos.x + 24, (int) uiPos.y + 8, true, this));
        this.uiInfoTabButton = addClickableWidget(new UIInfoTabButton((int) uiPos.x, (int) (uiPos.y + 8), false, this));
        this.tabButtons = new UITabButton[]{uiHealthTabButton, uiInfoTabButton};
    }

    @Override
    public void renderBackground(DrawContext context) {
        context.drawTexture(new Identifier(ZomboidHealthSystem.ID,"textures/gui/ui_texture.png"),
                (int) uiPos.x,
                (int) uiPos.y,0,0,
                (int)(498 / 2.5F),(int)(462 / 2.5F),
                (int)(498 / 2.5F),(int)(462 / 2.5F)
        );
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float tickDelta) {
        renderBackground(context);

        uiHealthTabButton.setPosition((int) uiPos.x + 24, (int) uiPos.y + 8);
        uiInfoTabButton.setPosition((int) uiPos.x, (int) uiPos.y + 8);
        bodyPartListWidget.setPosition((int) uiPos.x + 75, (int) uiPos.y + 45);

        bodyPartsWidget.visible = !uiHealthTabButton.isEnabled;
        bodyPartListWidget.visible = uiHealthTabButton.isEnabled;

        if(uiHealthTabButton.isEnabled) {
            context.drawTexture(Identifier.of(ZomboidHealthSystem.ID, "textures/gui/heart.png"),
                    (int) uiPos.x + 62, (int) uiPos.y + 123, 0, 0, 11, 10, 11, 10
            );

            context.drawText(
                    MinecraftClient.getInstance().textRenderer,
                    Text.translatable("zomboidhealthsystem.health.hud.body_status"),
                    (int) uiPos.x + 78,
                    (int) uiPos.y + 25,
                    0xFFf9f0f0,false
            );
            context.drawText(
                    MinecraftClient.getInstance().textRenderer,
                    damageText,
                    (int) uiPos.x + 78,
                    (int) uiPos.y + 35,
                    0xFFf9f0f0,
                    false
            );

            context.drawTexture(
                    new Identifier(ZomboidHealthSystem.ID,"textures/gui/line.png"),
                    (int) uiPos.x + 66,
                    (int) uiPos.y + 119 - (int) (ZomboidHealthSystemClient.HEALTH.getPlayerHp() * 0.7F),
                    0,0,5,
                    (int)(ZomboidHealthSystemClient.HEALTH.getPlayerHp() * 0.7F),7,90
            );

            ZomboidHealthSystemClient.HUD.getBodyPartsWidget().render(context, tickDelta, uiPos.add(14, 50, new Vector2f()));
        }
        if(uiInfoTabButton.isEnabled) {
            ClientWorldInfo worldInfo = ZomboidHealthSystemClient.WORLD_INFO;
            World world = MinecraftClient.getInstance().world;

            if(worldInfo.getDayLengthMultiplier() > 0) {
                context.drawText(
                        MinecraftClient.getInstance().textRenderer,
                        Text.of("Day: " + (((world.getTimeOfDay() + 8000) / 24000) + 1)),
                        (int) uiPos.x + 10, (int) uiPos.y + 25, 0xFFffffff, true
                );
            } else {
                context.drawText(
                        MinecraftClient.getInstance().textRenderer,
                        Text.of("Day: 1"),
                        (int) uiPos.x + 10, (int) uiPos.y + 25, 0xFFffffff, true
                );
            }

            context.drawText(
                    MinecraftClient.getInstance().textRenderer,
                    Text.of( "Time: " + new TimeOfDay(world.getTimeOfDay()).getModTime()),
                    (int) uiPos.x + 10, (int) uiPos.y + 35, 0xFFffffff, true
            );

            context.drawText(
                    MinecraftClient.getInstance().textRenderer,
                    Text.of("Temperature: " + worldInfo.getTemperature() + "°C"),
                    (int) uiPos.x + 10, (int) uiPos.y + 45, 0xFFffffff,true
            );

            context.drawText(
                    MinecraftClient.getInstance().textRenderer,
                    Text.of("World temperature: " + worldInfo.getWorldTemp() + "°C"),
                    (int) uiPos.x + 10, (int) uiPos.y + 55, 0xFFffffff,true
            );

            context.drawText(
                    MinecraftClient.getInstance().textRenderer,
                    Text.of("Season temperature: " + worldInfo.getSeasonTemp() + "°C"),
                    (int) uiPos.x + 10, (int) uiPos.y + 65, 0xFFffffff,true
            );

            context.drawText(
                    MinecraftClient.getInstance().textRenderer,
                    Text.of("Wind: " + worldInfo.getWind() + "m/s"),
                    (int) uiPos.x + 10, (int) uiPos.y + 75, 0xFFffffff,true
            );
        }

        super.render(context, mouseX, mouseY, tickDelta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(keyCode == ModKeyBindings.HEALTH.getDefaultKey().getCode()){
            this.close();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void close() {
        super.close();
        LAST_POS = uiPos;
    }

    @Override
    public void tick() {
        super.tick();
        bodyPartListWidget.tick();

        float sumHp = ZomboidHealthSystemClient.HEALTH.getBodyHpPercent();

        if (sumHp >= 1.0F) {
            damageText = Text.translatable("zomboidhealthsystem.health.hud.completely_health");
        }
        else if (sumHp >= 0.93F) {
            damageText = Text.translatable("zomboidhealthsystem.health.hud.minor_damage");
        }
        else if (sumHp >= 0.75F) {
            damageText = Text.translatable("zomboidhealthsystem.health.hud.average_damage");
        }
        else if (sumHp >= 0.55F) {
            damageText = Text.translatable("zomboidhealthsystem.health.hud.severe_damage");
        }
        else {
            damageText = Text.translatable("zomboidhealthsystem.health.hud.at_death");
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(mouseX > uiPos.x && mouseX < uiPos.x + 8 && mouseY > uiPos.y && mouseY < uiPos.y + 8) {
            this.close();
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        double lastMouseX = mouseX - deltaX, lastMouseY = mouseY - deltaY;

        if(lastMouseX > uiPos.x + 8 && lastMouseX < uiPos.x + (int) (498 / 2.5F) && lastMouseY > uiPos.y && lastMouseY < uiPos.y + 8) {
            uiPos.x += (float) deltaX;
            uiPos.y += (float) deltaY;
            return true;
        } else {
            return false;
        }
    }
}
