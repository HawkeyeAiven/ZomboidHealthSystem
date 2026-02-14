package aiven.zomboidhealthsystem.foundation.mixin;

import aiven.zomboidhealthsystem.foundation.utility.Util;
import com.google.common.collect.Ordering;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
@Mixin(value = AbstractInventoryScreen.class, priority = 500)
public abstract class AbstractInventoryScreenMixin extends HandledScreenMixin {
    @Shadow protected abstract void drawStatusEffectBackgrounds(DrawContext context, int x, int height, Iterable<StatusEffectInstance> statusEffects, boolean wide);

    @Shadow protected abstract void drawStatusEffectSprites(DrawContext context, int x, int height, Iterable<StatusEffectInstance> statusEffects, boolean wide);

    @Shadow protected abstract void drawStatusEffectDescriptions(DrawContext context, int x, int height, Iterable<StatusEffectInstance> statusEffects);

    @Shadow protected abstract Text getStatusEffectDescription(StatusEffectInstance statusEffect);

    /**
     * @author Aiven
     * @reason Привет
     */
    @Overwrite
    private void drawStatusEffects(DrawContext context, int mouseX, int mouseY) {
        int i = this.x + this.backgroundWidth + 2;
        int j = this.width - i;
        Collection<StatusEffectInstance> collection = this.client.player.getStatusEffects();
        if (!collection.isEmpty() && j >= 32) {
            boolean bl = j >= 120;
            int k = 33;
            if (collection.size() > 5) {
                k = 132 / (collection.size() - 1);
            }

            Iterable<StatusEffectInstance> iterable = Util.sortEffects(collection);
            this.drawStatusEffectBackgrounds(context, i, k, iterable, bl);
            this.drawStatusEffectSprites(context, i, k, iterable, bl);
            if (bl) {
                this.drawStatusEffectDescriptions(context, i, k, iterable);
            } else if (mouseX >= i && mouseX <= i + 33) {
                int l = this.y;
                StatusEffectInstance statusEffectInstance = null;

                for(StatusEffectInstance statusEffectInstance2 : iterable) {
                    if (mouseY >= l && mouseY <= l + k) {
                        statusEffectInstance = statusEffectInstance2;
                    }

                    l += k;
                }

                if (statusEffectInstance != null) {
                    List<Text> list = List.of(this.getStatusEffectDescription(statusEffectInstance), StatusEffectUtil.getDurationText(statusEffectInstance, 1.0F));
                    context.drawTooltip(this.textRenderer, list, Optional.empty(), mouseX, mouseY);
                }
            }

        }
    }
}
