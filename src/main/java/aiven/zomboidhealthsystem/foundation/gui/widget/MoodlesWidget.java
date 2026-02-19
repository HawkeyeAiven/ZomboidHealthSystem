package aiven.zomboidhealthsystem.foundation.gui.widget;

import aiven.zomboidhealthsystem.Config;
import aiven.zomboidhealthsystem.ZomboidHealthSystem;
import aiven.zomboidhealthsystem.ZomboidHealthSystemClient;
import aiven.zomboidhealthsystem.foundation.player.Health;
import aiven.zomboidhealthsystem.foundation.player.moodles.Moodle;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.joml.Vector2f;

import java.util.ArrayList;

public class MoodlesWidget extends ModClickableWidget {
    private static final int SIZE = Config.MOODLE_ICON_SIZE.getValue();

    private final ArrayList<String> iconNames = new ArrayList<>();
    private final ModClickableWidget iconName = new ModClickableWidget(0,0,0,20,null);
    private final Vector2f pos;

    public MoodlesWidget() {
        super(0, Config.MOODLE_POSITION_Y.getValue(), SIZE, 0, null);
        this.pos = new Vector2f(0, Config.MOODLE_POSITION_Y.getValue());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.setX(MinecraftClient.getInstance().getWindow().getScaledWidth() - SIZE);
        Health health = ZomboidHealthSystemClient.HEALTH;
        this.height = 0;
        this.iconNames.clear();

        if(health == null) {
            return;
        }

        if(health.isBleeding() && Config.SHOW_BLEEDING_ICON.getValue()) {
            context.drawTexture(
                    Identifier.of(ZomboidHealthSystem.ID, "textures/moodle/moodle_bkg_bad_2.png"),
                    this.getX(), this.getY() + getHeight(), 0,0, SIZE, SIZE, SIZE, SIZE
            );

            context.drawTexture(
                    Identifier.of(ZomboidHealthSystem.ID, "textures/moodle/moodle_icon_bleeding.png"),
                    this.getX(), this.getY() + getHeight(), 0,0, SIZE, SIZE, SIZE, SIZE
            );

            this.height += SIZE;

            this.iconNames.add(Text.translatable("zomboidhealthsystem.text.bleeding").getString());
        }

        for(Moodle moodle : health.getSortMoodleArray()) {
            if(moodle.showIcon()) {
                int amplifier = moodle.getAmplifier();
                amplifier = Math.min(amplifier, 4);
                amplifier = Math.max(amplifier, -4);
                if(amplifier > 0) {
                    context.drawTexture(
                            Identifier.of(ZomboidHealthSystem.ID, "textures/moodle/moodle_bkg_bad_%s.png".formatted(amplifier)),
                            this.getX(), this.getY() + getHeight(), 0,0, SIZE, SIZE, SIZE, SIZE
                    );
                } else {
                    context.drawTexture(
                            Identifier.of(ZomboidHealthSystem.ID, "textures/moodle/moodle_bkg_good_%s.png".formatted(-amplifier)),
                            this.getX(), this.getY() + getHeight(), 0,0, SIZE, SIZE, SIZE, SIZE
                    );
                }
                context.drawTexture(
                        moodle.getMoodleIconTexture(),
                        this.getX(), this.getY() + getHeight(), 0, 0, SIZE, SIZE, SIZE, SIZE
                        );

                this.height += SIZE;

                this.iconNames.add(Text.translatable("zomboidhealthsystem.text.%s".formatted(moodle.getId())).getString() + " " + Math.abs(moodle.getAmplifier()));
            }
        }
        if(health.getTemperature().isFeelingHot() || health.getTemperature().isFeelingCold()) {
            context.drawTexture(
                    Identifier.of(ZomboidHealthSystem.ID, "textures/moodle/moodle_bkg_bad_1.png"),
                    this.getX(), this.getY() + getHeight(), 0,0, SIZE, SIZE, SIZE, SIZE
            );
            if(health.getTemperature().isFeelingHot()) {
                context.drawTexture(
                        Identifier.of(ZomboidHealthSystem.ID, "textures/moodle/moodle_icon_hot_weather.png"),
                        this.getX(), this.getY() + getHeight(), 0,0, SIZE, SIZE, SIZE, SIZE
                );
                this.iconNames.add(Text.translatable("zomboidhealthsystem.text.hot_weather").getString());
            } else {
                context.drawTexture(
                        Identifier.of(ZomboidHealthSystem.ID, "textures/moodle/moodle_icon_cold_weather.png"),
                        this.getX(), this.getY() + getHeight(), 0,0, SIZE, SIZE, SIZE, SIZE
                );
                this.iconNames.add(Text.translatable("zomboidhealthsystem.text.cold_weather").getString());
            }
            this.height += SIZE;
        }

        if(Config.SHOW_INJURED_ICON.getValue()){
            float sumHp = health.getSumOfHp();
            float maxSumHp = health.getMaxSumOfHp();
            if (sumHp <= maxSumHp - 2) {
                int amplifier = (int) ((health.getMaxSumOfHp() - sumHp) / 2);
                amplifier = Math.min(amplifier, 4);

                context.drawTexture(
                        Identifier.of(ZomboidHealthSystem.ID, "textures/moodle/moodle_bkg_bad_%s.png".formatted(amplifier)),
                        this.getX(), this.getY() + getHeight(), 0,0, SIZE, SIZE, SIZE, SIZE
                );

                context.drawTexture(
                        Identifier.of(ZomboidHealthSystem.ID, "textures/moodle/moodle_icon_injured.png"),
                        this.getX(), this.getY() + getHeight(), 0,0, SIZE, SIZE, SIZE, SIZE
                );

                height += SIZE;

                this.iconNames.add(Text.translatable("zomboidhealthsystem.text.injured").getString());
            }
        }
        if(iconName.visible) {
            iconName.render(context, mouseX, mouseY, delta);
        }
    }

    public void setPos(Vector2f pos) {
        this.pos.set(pos);
    }

    @Override
    public void setX(int x) {
        this.pos.x = x;
    }

    @Override
    public void setY(int y) {
        this.pos.y = y;
    }

    @Override
    public int getX() {
        return (int) pos.x;
    }

    @Override
    public int getY() {
        return (int) pos.y;
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        if(isMouseOver(mouseX, mouseY)) {
            int iconIndex = (int) (mouseY - getY()) / SIZE;
            Text text = Text.of(iconNames.get(iconIndex));
            int width = Math.max(MinecraftClient.getInstance().textRenderer.getWidth(text), 40);
            iconName.setX((int) mouseX - width);
            iconName.setY((int) mouseY);
            iconName.setWidth(width + 6);
            iconName.setMessage(text);
            iconName.visible = true;
        } else {
            iconName.visible = false;
        }
        super.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        double lastMouseX = mouseX - deltaX;
        double lastMouseY = mouseY - deltaY;
        if(isMouseOver(lastMouseX, lastMouseY) && button == 0) {
            this.pos.add(0, (float) deltaY);
            return true;
        } else {
            return false;
        }
    }
}
