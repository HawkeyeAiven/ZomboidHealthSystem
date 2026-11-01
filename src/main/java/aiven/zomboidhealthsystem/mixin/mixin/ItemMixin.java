package aiven.zomboidhealthsystem.mixin.mixin;

import aiven.zomboidhealthsystem.ZomboidHealthSystem;
import aiven.zomboidhealthsystem.health.Health;
import aiven.zomboidhealthsystem.health.moodles.Hunger;
import aiven.zomboidhealthsystem.mixin.access.PlayerAccess;
import aiven.zomboidhealthsystem.utility.ItemUtil;
import aiven.zomboidhealthsystem.utility.Util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.Nullable;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = Item.class)
public abstract class ItemMixin {

    @Shadow
    @Final
    private @Nullable FoodProperties foodProperties;

    @Shadow
    public abstract Item asItem();

    @Inject(at = @At("HEAD"), method = "appendHoverText")
    private void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag, CallbackInfo ci) {
        if(foodProperties != null && foodProperties.getNutrition() > 0) {
            float hunger = (float) Util.floor(foodProperties.getNutrition() * Hunger.DEFAULT_APPETITE, 2);
            Component component = Component.translatable(ZomboidHealthSystem.ID + ".tooltip.hunger", hunger).withStyle(ChatFormatting.GRAY);
            list.add(component);
        }

        float thirst = (float) Util.floor(ItemUtil.getThirst(asItem()), 2);
        if(thirst != -1) {
            Component component = Component.translatable(ZomboidHealthSystem.ID + ".tooltip.thirst", thirst).withStyle(ChatFormatting.GRAY);
            list.add(component);
        }
    }


    @Inject(at = @At("HEAD"), method = "finishUsingItem")
    private void finishUsingItem(ItemStack itemStack, Level level, LivingEntity livingEntity, CallbackInfoReturnable<ItemStack> cir) {
        float thirst = ItemUtil.getThirst(asItem());
        if(thirst != -1 && !level.isClientSide() && livingEntity instanceof PlayerAccess playerAccess) {
            Health health = playerAccess.zomboid_health_system$getModHealth();
            health.getThirst().drink(thirst, true);
        }
    }
}
