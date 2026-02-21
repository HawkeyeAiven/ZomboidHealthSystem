package aiven.zomboidhealthsystem.foundation.gui.screen;

import aiven.zomboidhealthsystem.ZomboidHealthSystem;
import aiven.zomboidhealthsystem.ZomboidHealthSystemClient;
import aiven.zomboidhealthsystem.foundation.gui.widget.BodyPartListWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.joml.Vector2f;


@Environment(EnvType.CLIENT)
public non-sealed class UIHealth extends UI {
    public static MutableText damage = Text.translatable("zomboidhealthsystem.health.hud.completely_health");

    public BodyPartListWidget bodyPartListWidget;

    public UIHealth(Vector2f ui) {
        super(ui);
        bodyPartListWidget = new BodyPartListWidget((int) ui.x + 75, (int) ui.y + 45);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float tickDelta) {
        context.drawTexture(new Identifier(ZomboidHealthSystem.ID,"textures/gui/texture.png"),
                (int) pos.x,
                (int) pos.y,0,0,
                (int)(498 / 2.5f),(int)(462 / 2.5f),
                (int)(498 / 2.5f),(int)(462 / 2.5f)
        );

        context.drawTexture(new Identifier(ZomboidHealthSystem.ID,"textures/gui/tab.png"),
                (int) pos.x,
                (int) pos.y + 8,
                0,0,24,10,24,10
        );

        context.drawTexture(new Identifier(ZomboidHealthSystem.ID,"textures/gui/on_tab.png"),
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

        context.drawText(
                MinecraftClient.getInstance().textRenderer,
                Text.translatable("zomboidhealthsystem.health.hud.body_status"),
                (int) pos.x + 78,
                (int) pos.y + 25,
                0xFFf9f0f0,false
        );
        context.drawText(
                MinecraftClient.getInstance().textRenderer,
                damage,
                (int) pos.x + 78,
                (int) pos.y + 35,
                0xFFf9f0f0,
                false
        );

        context.drawTexture(
                new Identifier(ZomboidHealthSystem.ID,"textures/gui/line.png"),
                (int) pos.x + 66,
                (int) pos.y + 119 - (int) (ZomboidHealthSystemClient.HEALTH.getPlayerHp() * 0.7F),
                0,0,5,
                (int)(ZomboidHealthSystemClient.HEALTH.getPlayerHp() * 0.7F),7,90
        );

        ZomboidHealthSystemClient.HUD.getBodyPartsWidget().render(context, tickDelta, pos.add(14, 50, new Vector2f()));

        bodyPartListWidget.setX((int) pos.x + 75);
        bodyPartListWidget.setY((int) pos.y + 45);

        super.render(context, mouseX, mouseY, tickDelta);
    }

    @Override
    public void init() {
        addClickableWidget(bodyPartListWidget);
        addClickableWidget(ZomboidHealthSystemClient.HUD.getTimeWidget());
        addClickableWidget(ZomboidHealthSystemClient.HUD.getMoodlesWidget());
    }

    @Override
    public void close() {
        super.close();
    }

    private int ticks = 0;
    @Override
    public void tick() {
        bodyPartListWidget.tick();

        if(ticks >= ZomboidHealthSystem.UPDATE_FREQUENCY){
            float sumHp = ZomboidHealthSystemClient.HEALTH.getBodyHpPercent();

            if(sumHp >= 1.0F) damage = Text.translatable("zomboidhealthsystem.health.hud.completely_health");
            else if(sumHp >= 0.93F) damage = Text.translatable("zomboidhealthsystem.health.hud.minor_damage");
            else if(sumHp >= 0.75F) damage = Text.translatable("zomboidhealthsystem.health.hud.average_damage");
            else if(sumHp >= 0.55F) damage = Text.translatable("zomboidhealthsystem.health.hud.severe_damage");
            else damage = Text.translatable("zomboidhealthsystem.health.hud.at_death");

            ticks = 0;
        }
        ticks++;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(mouseX > pos.x && mouseX < pos.x + 24 && mouseY > pos.y + 8 && mouseY < pos.y + 8 + 8) {
            client.setScreen(new UIInfo(pos));
            client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}