package aiven.zomboidhealthsystem.mixin.mixin.client;

import aiven.zomboidhealthsystem.gui.screen.ModScreen;
import aiven.zomboidhealthsystem.item.AbstractBandageItem;

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = AbstractBandageItem.class)
public class AbstractBandageItemMixin {

    @Inject(at = @At("RETURN"), method = "use")
    public void use(Level level, Player player, InteractionHand interactionHand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        try {
            if(cir.getReturnValue().getResult().equals(InteractionResult.PASS)) {
                Minecraft.getInstance().setScreen(new ModScreen());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
