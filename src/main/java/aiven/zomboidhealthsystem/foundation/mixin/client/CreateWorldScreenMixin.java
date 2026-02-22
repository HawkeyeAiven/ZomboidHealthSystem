package aiven.zomboidhealthsystem.foundation.mixin.client;

import aiven.zomboidhealthsystem.foundation.gui.widget.WorldPropertiesButton;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.widget.ClickableWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(value = CreateWorldScreen.class, priority = 2000)
public abstract class CreateWorldScreenMixin extends ScreenMixin {
    @Shadow protected abstract <T extends Element & Selectable> T addSelectableChild(T child);

    @Shadow protected abstract <T extends Element & Drawable & Selectable> T addDrawableChild(T drawableElement);

    @Inject(at = @At("HEAD"), method = "init")
    private void init(CallbackInfo ci){
        ClickableWidget button = addSelectableChild(new WorldPropertiesButton(20, MinecraftClient.getInstance().getWindow().getScaledHeight() - 75));
        addDrawable(button);
    }
}
