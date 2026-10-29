package aiven.zomboidhealthsystem.mixin.mixin.client;

import aiven.zomboidhealthsystem.ZomboidHealthSystemClient;

import net.minecraft.client.gui.screens.inventory.InventoryScreen;

import net.minecraft.world.entity.player.Player;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = InventoryScreen.class)
public abstract class InventoryScreenMixin extends EffectRenderingInventoryScreenMixin {

    @Inject(at = @At("RETURN"), method = "<init>")
    public void init(Player player, CallbackInfo ci) {
        addRenderableWidget(ZomboidHealthSystemClient.getHud());
    }
}
