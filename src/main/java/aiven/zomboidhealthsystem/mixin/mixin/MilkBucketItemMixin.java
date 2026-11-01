package aiven.zomboidhealthsystem.mixin.mixin;

import aiven.zomboidhealthsystem.health.Health;
import aiven.zomboidhealthsystem.mixin.access.PlayerAccess;
import aiven.zomboidhealthsystem.utility.ItemUtil;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MilkBucketItem;
import net.minecraft.world.level.Level;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = MilkBucketItem.class)
public abstract class MilkBucketItemMixin extends ItemMixin {


    @Inject(at = @At("RETURN"), method = "finishUsingItem")
    private void finishUsingItem(ItemStack itemStack, Level level, LivingEntity livingEntity, CallbackInfoReturnable<ItemStack> cir) {
        float thirst = ItemUtil.getThirst(asItem());
        if(thirst != -1 && !level.isClientSide() && livingEntity instanceof PlayerAccess playerAccess) {
            Health health = playerAccess.zomboid_health_system$getModHealth();
            health.getThirst().drink(thirst, true);
        }
    }
}
