package aiven.zomboidhealthsystem.foundation.mixin;


import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = HandledScreen.class, priority = 500)
public abstract class HandledScreenMixin extends ScreenMixin {
    @Shadow protected int y;
    @Shadow protected int x;
    @Shadow protected int backgroundWidth;
}
