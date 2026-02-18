package aiven.zomboidhealthsystem.foundation.gui.widget;

import aiven.zomboidhealthsystem.ZomboidHealthSystem;
import aiven.zomboidhealthsystem.ZomboidHealthSystemClient;
import aiven.zomboidhealthsystem.foundation.player.Health;
import aiven.zomboidhealthsystem.foundation.player.moodles.Moodle;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

public class MoodlesWidget extends ModClickableWidget {
    private static final int SIZE = 16;

    public MoodlesWidget() {
        super(0, 70, 0, 0, null);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.setX(MinecraftClient.getInstance().getWindow().getScaledWidth() - SIZE);
        Health health = ZomboidHealthSystemClient.HEALTH;
        this.height = 0;
        for(Moodle moodle : health.getMoodles()) {
            int amount = moodle.getEffectAmplifier() + 1;
            if(amount != 0) {
                amount = Math.min(amount, 4);
                amount = Math.max(amount, -4);
                if(amount > 0) {
                    context.drawTexture(
                            Identifier.of(ZomboidHealthSystem.ID, "textures/moodle/moodle_bkg_bad_%s.png".formatted(amount)),
                            this.getX(), this.getY() + getHeight(), 0,0, SIZE, SIZE, SIZE, SIZE
                    );
                } else {
                    context.drawTexture(
                            Identifier.of(ZomboidHealthSystem.ID, "textures/moodle/moodle_bkg_good_%s.png".formatted(-amount)),
                            this.getX(), this.getY() + getHeight(), 0,0, SIZE, SIZE, SIZE, SIZE
                    );
                }
                context.drawTexture(
                        Identifier.of(ZomboidHealthSystem.ID, "textures/moodle/moodle_icon_%s.png".formatted(moodle.getId())),
                        this.getX(), this.getY() + getHeight(), 0, 0, SIZE, SIZE, SIZE, SIZE
                        );

                this.height += SIZE;
            }
        }
    }
}
