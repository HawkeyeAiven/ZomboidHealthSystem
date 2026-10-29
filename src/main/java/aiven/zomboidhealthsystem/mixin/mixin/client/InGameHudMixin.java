package aiven.zomboidhealthsystem.mixin.mixin.client;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = Gui.class, priority = 500)
public abstract class InGameHudMixin {
    @Shadow
    protected abstract Player getCameraPlayer();

    @Shadow
    private int screenWidth;

    @Shadow
    @Final
    private static ResourceLocation GUI_ICONS_LOCATION;

    @Shadow
    private int screenHeight;

    /**
     * @author Aiven
     * @reason Не реалистично, не хардкорно, не по мужски.
     */
    @Overwrite
    private void renderPlayerHealth(GuiGraphics guiGraphics) {
        int m = this.screenWidth / 2 - 91;
        int o = this.screenHeight - 39;

        int y = o;
        int u = getCameraPlayer().getArmorValue();

        for (int w = 0; w < 10; w++) {
            if (u > 0) {
                int x = m + w * 8;
                if (w * 2 + 1 < u) {
                    guiGraphics.blit(GUI_ICONS_LOCATION, x, y, 34, 9, 9, 9);
                }

                if (w * 2 + 1 == u) {
                    guiGraphics.blit(GUI_ICONS_LOCATION, x, y, 25, 9, 9, 9);
                }

                if (w * 2 + 1 > u) {
                    guiGraphics.blit(GUI_ICONS_LOCATION, x, y, 16, 9, 9, 9);
                }
            }
        }
    }
}
