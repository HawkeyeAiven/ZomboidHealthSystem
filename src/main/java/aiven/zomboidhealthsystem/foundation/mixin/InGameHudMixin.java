package aiven.zomboidhealthsystem.foundation.mixin;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.StatusEffectSpriteManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
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

import java.util.Collection;
import java.util.List;

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

    /**
     * @author Aiven
     * @reason Таков путь
     */
    @Overwrite
    public void renderStatusEffectOverlay(DrawContext context) {
        Collection<StatusEffectInstance> collection = this.client.player.getStatusEffects();
        if (!collection.isEmpty()) {
            Screen var4 = this.client.currentScreen;
            if (var4 instanceof AbstractInventoryScreen) {
                AbstractInventoryScreen abstractInventoryScreen = (AbstractInventoryScreen)var4;
                if (abstractInventoryScreen.hideStatusEffectHud()) {
                    return;
                }
            }

            RenderSystem.enableBlend();
            int i = 0;
            int j = 0;
            StatusEffectSpriteManager statusEffectSpriteManager = this.client.getStatusEffectSpriteManager();
            List<Runnable> list = Lists.newArrayListWithExpectedSize(collection.size());

            for(StatusEffectInstance statusEffectInstance : aiven.zomboidhealthsystem.foundation.utility.Util.sortEffects(collection)) {
                StatusEffect statusEffect = statusEffectInstance.getEffectType();
                if (statusEffectInstance.shouldShowIcon()) {
                    int k = this.scaledWidth;
                    int l = 1;
                    if (this.client.isDemo()) {
                        l += 15;
                    }

                    if (statusEffect.isBeneficial()) {
                        ++i;
                        k -= 25 * i;
                    } else {
                        ++j;
                        k -= 25 * j;
                        l += 26;
                    }

                    float f;
                    if (statusEffectInstance.isAmbient()) {
                        f = 1.0F;
                        context.drawTexture(HandledScreen.BACKGROUND_TEXTURE, k, l, 165, 166, 24, 24);
                    } else {
                        context.drawTexture(HandledScreen.BACKGROUND_TEXTURE, k, l, 141, 166, 24, 24);
                        if (statusEffectInstance.isDurationBelow(200)) {
                            int m = statusEffectInstance.getDuration();
                            int n = 10 - m / 20;
                            f = MathHelper.clamp((float)m / 10.0F / 5.0F * 0.5F, 0.0F, 0.5F) + MathHelper.cos((float)m * (float)Math.PI / 5.0F) * MathHelper.clamp((float)n / 10.0F * 0.25F, 0.0F, 0.25F);
                        } else {
                            f = 1.0F;
                        }
                    }

                    Sprite sprite = statusEffectSpriteManager.getSprite(statusEffect);
                    int finalK = k;
                    int finalL = l;
                    list.add((Runnable)() -> {
                        context.setShaderColor(1.0F, 1.0F, 1.0F, f);
                        context.drawSprite(finalK + 3, finalL + 3, 0, 18, 18, sprite);
                        context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                    });
                }
            }

            list.forEach(Runnable::run);
        }
    }
}
