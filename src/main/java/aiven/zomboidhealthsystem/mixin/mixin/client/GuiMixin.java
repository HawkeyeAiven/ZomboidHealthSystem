package aiven.zomboidhealthsystem.mixin.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = Gui.class, priority = 500)
public abstract class GuiMixin {
    @Shadow
    protected abstract Player getCameraPlayer();

    @Shadow
    private int screenWidth;

    @Shadow
    @Final
    private static ResourceLocation GUI_ICONS_LOCATION;

    @Shadow
    private int screenHeight;

    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    private int tickCount;

    @Shadow
    private int lastHealth;

    @Shadow
    @Final
    private RandomSource random;

    @Shadow
    protected abstract LivingEntity getPlayerVehicleWithHealth();

    @Shadow
    protected abstract int getVehicleMaxHearts(LivingEntity livingEntity);

    @Shadow
    protected abstract int getVisibleVehicleHeartRows(int i);

    /**
     * @author Aiven
     * @reason Не реалистично, не хардкорно, не по мужски.
     */
    @Overwrite
    private void renderPlayerHealth(GuiGraphics guiGraphics) {
        Player player = this.getCameraPlayer();
        if (player != null) {
            int i = Mth.ceil(player.getHealth());
            this.lastHealth = i;
            int j = 0;
            this.random.setSeed(this.tickCount * 312871L);
            int m = this.screenWidth / 2 - 91;
            int n = this.screenWidth / 2 + 91;
            int o = this.screenHeight - 39;
            float f = Math.max((float) player.getAttributeValue(Attributes.MAX_HEALTH), Math.max(j, i));
            int p = Mth.ceil(player.getAbsorptionAmount());
            int q = Mth.ceil((f + p) / 2.0F / 10.0F);
            int r = Math.max(10 - (q - 2), 3);
            int s = o - (q - 1) * r;
            int t = o - 10;
            int u = player.getArmorValue();

            this.minecraft.getProfiler().push("armor");

            for (int w = 0; w < 10; w++) {
                if (u > 0) {
                    int x = m + w * 8;
                    if (w * 2 + 1 < u) {
                        guiGraphics.blit(GUI_ICONS_LOCATION, x, s, 34, 9, 9, 9);
                    }

                    if (w * 2 + 1 == u) {
                        guiGraphics.blit(GUI_ICONS_LOCATION, x, s, 25, 9, 9, 9);
                    }

                    if (w * 2 + 1 > u) {
                        guiGraphics.blit(GUI_ICONS_LOCATION, x, s, 16, 9, 9, 9);
                    }
                }
            }

            LivingEntity livingEntity = this.getPlayerVehicleWithHealth();
            int x = this.getVehicleMaxHearts(livingEntity);

            this.minecraft.getProfiler().popPush("air");
            int y = player.getMaxAirSupply();
            int z = Math.min(player.getAirSupply(), y);
            if (player.isEyeInFluid(FluidTags.WATER) || z < y) {
                int aa = this.getVisibleVehicleHeartRows(x) - 1;
                t -= aa * 10;
                int ab = Mth.ceil((z - 2) * 10.0 / y);
                int ac = Mth.ceil(z * 10.0 / y) - ab;

                for (int ad = 0; ad < ab + ac; ad++) {
                    if (ad < ab) {
                        guiGraphics.blit(GUI_ICONS_LOCATION, n - ad * 8 - 9, t, 16, 18, 9, 9);
                    } else {
                        guiGraphics.blit(GUI_ICONS_LOCATION, n - ad * 8 - 9, t, 25, 18, 9, 9);
                    }
                }
            }

            this.minecraft.getProfiler().pop();
        }
    }
}
