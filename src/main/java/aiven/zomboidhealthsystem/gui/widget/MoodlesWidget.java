package aiven.zomboidhealthsystem.gui.widget;

import aiven.zomboidhealthsystem.Config;
import aiven.zomboidhealthsystem.ZomboidHealthSystem;
import aiven.zomboidhealthsystem.health.Health;
import aiven.zomboidhealthsystem.health.moodles.Moodle;
import aiven.zomboidhealthsystem.utility.ClientUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;

public class MoodlesWidget extends AbstractMovableWidget {
    private static final int SIZE = Config.getInstance().moodle_icon_size;

    private final ArrayList<String> iconNames = new ArrayList<>();

    public MoodlesWidget(int x, int y) {
        super(x, y, SIZE, 0, Component.empty());
    }

    @Override
    public boolean isRelativeContent() {
        return false;
    }

    @Override
    protected void renderWidget(GuiGraphics context, int mouseX, int mouseY, float tickDelta) {
        this.height = 0;
        this.iconNames.clear();

        Health health = ClientUtil.getHealth();

        if(health == null) {
            return;
        }

        if(Minecraft.getInstance().player.isCreative() || Minecraft.getInstance().player.isSpectator()) {
            return;
        }

        for(Moodle moodle : health.getSortMoodleArray()) {
            if(moodle.showIcon()) {
                context.blit(
                        moodle.getBackgroundTexture(),
                        this.getX(), this.getY() + getHeight(), 0,0, SIZE, SIZE, SIZE, SIZE
                );
                context.blit(
                        moodle.getMoodleIconTexture(),
                        this.getX(), this.getY() + getHeight(), 0, 0, SIZE, SIZE, SIZE, SIZE
                );
                this.height += SIZE;
                this.iconNames.add(moodle.getMoodleIconText());
            }
        }
        if(health.getTemperature().isFeelingHot() || health.getTemperature().isFeelingCold()) {
            context.blit(
                    ZomboidHealthSystem.getResourceLocation("textures/moodle/moodle_bkg_bad_1.png"),
                    this.getX(), this.getY() + getHeight(), 0,0, SIZE, SIZE, SIZE, SIZE
            );
            if(health.getTemperature().isFeelingHot()) {
                context.blit(
                        ZomboidHealthSystem.getResourceLocation("textures/moodle/moodle_icon_hot_weather.png"),
                        this.getX(), this.getY() + getHeight(), 0,0, SIZE, SIZE, SIZE, SIZE
                );
                this.iconNames.add(Component.translatable("zomboid-health-system.moodle.hot_weather").getString());
            } else {
                context.blit(
                        ZomboidHealthSystem.getResourceLocation("textures/moodle/moodle_icon_cold_weather.png"),
                        this.getX(), this.getY() + getHeight(), 0,0, SIZE, SIZE, SIZE, SIZE
                );
                this.iconNames.add(Component.translatable("zomboid-health-system.moodle.cold_weather").getString());
            }
            this.height += SIZE;
        }

        if(Config.getInstance().show_injured_icon){
            float sumHp = health.getHpSum();
            float maxSumHp = health.getMaxHpSum();
            if (sumHp <= maxSumHp - 2) {
                int amplifier = (int) ((health.getMaxHpSum() - sumHp) / 2);
                amplifier = Math.min(amplifier, 4);

                context.blit(
                        ZomboidHealthSystem.getResourceLocation("textures/moodle/moodle_bkg_bad_%s.png".formatted(amplifier)),
                        this.getX(), this.getY() + getHeight(), 0,0, SIZE, SIZE, SIZE, SIZE
                );

                context.blit(
                        ZomboidHealthSystem.getResourceLocation("textures/moodle/moodle_icon_injured.png"),
                        this.getX(), this.getY() + getHeight(), 0,0, SIZE, SIZE, SIZE, SIZE
                );

                height += SIZE;

                this.iconNames.add(Component.translatable("zomboid-health-system.moodle.injured").getString());
            }
        }
        if(isMouseOver(mouseX, mouseY)) {
            int iconIndex = (mouseY - getY()) / SIZE;
            String iconName = iconNames.get(iconIndex);
            Component text = Component.literal(iconName);
            int width = Math.max(Minecraft.getInstance().font.width(text) + 6, 40);
            context.blit(ZomboidHealthSystem.getResourceLocation("textures/gui/description_bkg.png"),
                    mouseX - width, mouseY, 0, 0, width, 20, width, 20
            );
            context.drawCenteredString(Minecraft.getInstance().font,
                    text, mouseX - (width / 2), mouseY + 6, 0xFFffff
            );
        }
    }
}
