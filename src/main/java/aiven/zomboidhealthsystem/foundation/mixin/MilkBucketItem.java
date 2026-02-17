package aiven.zomboidhealthsystem.foundation.mixin;

import aiven.zomboidhealthsystem.foundation.world.ModServer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = net.minecraft.item.MilkBucketItem.class)
public class MilkBucketItem {
    @Inject(at = @At("HEAD"), method = "finishUsing")
    private void finishUsing(ItemStack stack, World world, LivingEntity user, CallbackInfoReturnable<ItemStack> cir) {
        if(!world.isClient()) {
            if(user instanceof ServerPlayerEntity player) {
                ModServer.getHealth(player).getThirst().drink(2.0F, true);
            }
        }
    }
}
