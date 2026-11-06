package aiven.zomboidhealthsystem.mixin.mixin.client;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin extends ScreenMixin {
}
