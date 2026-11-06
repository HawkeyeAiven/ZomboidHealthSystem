package aiven.zomboidhealthsystem.mixin.mixin.client;

import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;

import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = EffectRenderingInventoryScreen.class)
public abstract class EffectRenderingInventoryScreenMixin extends AbstractContainerScreenMixin {
}
