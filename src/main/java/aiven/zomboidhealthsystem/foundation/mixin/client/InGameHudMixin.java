package aiven.zomboidhealthsystem.foundation.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Environment(EnvType.CLIENT)
@Mixin(value = InGameHud.class, priority = 500)
public abstract class InGameHudMixin {
    @Shadow protected abstract PlayerEntity getCameraPlayer();

    @Shadow private long heartJumpEndTick;

    @Shadow private int ticks;

    @Shadow private int lastHealthValue;

    @Shadow private long lastHealthCheckTime;

    @Shadow private int renderHealthValue;

    @Shadow @Final private Random random;

    @Shadow private int scaledWidth;

    @Shadow private int scaledHeight;

    @Shadow @Final private MinecraftClient client;

    @Shadow @Final private static Identifier ICONS;

    @Shadow protected abstract LivingEntity getRiddenEntity();

    @Shadow protected abstract int getHeartCount(LivingEntity entity);

    @Shadow protected abstract int getHeartRows(int heartCount);

    /**
     * @author Aiven
     * @reason Yes.
     */
    @Overwrite
    private void renderStatusBars(DrawContext context) {
        PlayerEntity playerEntity = this.getCameraPlayer();
        if (playerEntity != null) {
            int i = MathHelper.ceil(playerEntity.getHealth());
            long l = Util.getMeasuringTimeMs();
            if (i < this.lastHealthValue && playerEntity.timeUntilRegen > 0) {
                this.lastHealthCheckTime = l;
                this.heartJumpEndTick = this.ticks + 20;
            } else if (i > this.lastHealthValue && playerEntity.timeUntilRegen > 0) {
                this.lastHealthCheckTime = l;
                this.heartJumpEndTick = this.ticks + 10;
            }

            if (l - this.lastHealthCheckTime > 1000L) {
                this.lastHealthValue = i;
                this.renderHealthValue = i;
                this.lastHealthCheckTime = l;
            }

            this.lastHealthValue = i;
            int j = this.renderHealthValue;
            this.random.setSeed((this.ticks * 312871L));
            int m = this.scaledWidth / 2 - 91;
            int n = this.scaledWidth / 2 + 91;
            int o = this.scaledHeight - 39;
            float f = Math.max((float)playerEntity.getAttributeValue(EntityAttributes.GENERIC_MAX_HEALTH), (float)Math.max(j, i));
            int p = MathHelper.ceil(playerEntity.getAbsorptionAmount());
            int q = MathHelper.ceil((f + (float)p) / 2.0F / 10.0F);
            int r = Math.max(10 - (q - 2), 3);
            int s = o - (q - 1) * r;
            int t = o - 10;
            int u = playerEntity.getArmor();

            this.client.getProfiler().push("armor");

            for(int w = 0; w < 10; ++w) {
                if (u > 0) {
                    int x = m + w * 8;
                    if (w * 2 + 1 < u) {
                        context.drawTexture(ICONS, x, s, 34, 9, 9, 9);
                    }

                    if (w * 2 + 1 == u) {
                        context.drawTexture(ICONS, x, s, 25, 9, 9, 9);
                    }

                    if (w * 2 + 1 > u) {
                        context.drawTexture(ICONS, x, s, 16, 9, 9, 9);
                    }
                }
            }

            this.client.getProfiler().swap("health");
            LivingEntity livingEntity = this.getRiddenEntity();
            int x = this.getHeartCount(livingEntity);
            this.client.getProfiler().swap("air");
            int y = playerEntity.getMaxAir();
            int z = Math.min(playerEntity.getAir(), y);
            if (playerEntity.isSubmergedIn(FluidTags.WATER) || z < y) {
                int aa = this.getHeartRows(x) - 1;
                t -= aa * 10;
                int ab = MathHelper.ceil((double)(z - 2) * (double)10.0F / (double)y);
                int ac = MathHelper.ceil((double)z * (double)10.0F / (double)y) - ab;

                for(int ad = 0; ad < ab + ac; ++ad) {
                    if (ad < ab) {
                        context.drawTexture(ICONS, n - ad * 8 - 9, t, 16, 18, 9, 9);
                    } else {
                        context.drawTexture(ICONS, n - ad * 8 - 9, t, 25, 18, 9, 9);
                    }
                }
            }

            this.client.getProfiler().pop();
        }
    }
}
