package aiven.zomboidhealthsystem.foundation.mixin;

import aiven.zomboidhealthsystem.foundation.world.ModServer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.potion.PotionUtil;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PotionItem.class)
public class PotionItemMixin {
    @Inject(at = @At("HEAD"), method = "finishUsing")
    private void init(ItemStack stack, World world, LivingEntity user, CallbackInfoReturnable<ItemStack> cir){
        if(!world.isClient && user instanceof PlayerEntity player){
            if(PotionUtil.getPotionEffects(stack).isEmpty()) {
                ModServer.getHealth(player).getThirst().drink(0.75F,false);
            } else {
                ModServer.getHealth(player).getThirst().drink(0.75F,true);
            }
        }
    }
}
