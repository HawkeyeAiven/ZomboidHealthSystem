package aiven.zomboidhealthsystem.foundation.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Screen.class)
public abstract class ScreenMixin {
    @Shadow protected TextRenderer textRenderer;
    @Shadow @Nullable protected MinecraftClient client;
    @Shadow public int width;

    @Shadow protected abstract <T extends Element & Selectable> T addSelectableChild(T child);

    @Shadow protected abstract <T extends Drawable> T addDrawable(T drawable);

    @Shadow protected abstract <T extends Element & Drawable & Selectable> T addDrawableChild(T drawableElement);
}
