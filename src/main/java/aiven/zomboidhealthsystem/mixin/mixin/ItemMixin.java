package aiven.zomboidhealthsystem.mixin.mixin;

import aiven.zomboidhealthsystem.Config;
import aiven.zomboidhealthsystem.ZomboidHealthSystem;
import aiven.zomboidhealthsystem.health.Health;
import aiven.zomboidhealthsystem.health.moodles.Hunger;
import aiven.zomboidhealthsystem.health.moodles.Temperature;
import aiven.zomboidhealthsystem.mixin.access.ItemAccess;
import aiven.zomboidhealthsystem.mixin.access.PlayerAccess;
import aiven.zomboidhealthsystem.utility.Util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ArmorItem;
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
public abstract class ItemMixin implements ItemAccess {

    @Shadow
    @Final
    private @Nullable FoodProperties foodProperties;

    @Shadow
    public abstract Item asItem();

    @Override
    @Nullable
    public Float zomboid_health_system$getThirst() {
        return Config.getInstance().thirst_values.get(Util.getStringId(asItem()));
    }

    @Inject(at = @At("HEAD"), method = "appendHoverText")
    private void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag, CallbackInfo ci) {
        if(foodProperties != null && foodProperties.getNutrition() > 0) {
            float hunger = (float) Util.floor(foodProperties.getNutrition() * Hunger.DEFAULT_APPETITE, 2);
            Component component = Component.translatable(ZomboidHealthSystem.ID + ".tooltip.hunger", hunger).withStyle(ChatFormatting.GRAY);
            list.add(component);
        }

        Float thirst = zomboid_health_system$getThirst();
        if(thirst != null) {
            float f = (float) Util.floor(thirst, 2);
            Component component = Component.translatable(ZomboidHealthSystem.ID + ".tooltip.thirst", f).withStyle(ChatFormatting.GRAY);
            list.add(component);
        }

        if(asItem() instanceof ArmorItem armorItem) {
            float heat = Temperature.getHeatFromArmor(armorItem);
            Component component = Component.translatable(ZomboidHealthSystem.ID + ".tooltip.heat", heat).withStyle(ChatFormatting.GRAY);
            list.add(component);
        }
    }

    @Inject(at = @At("RETURN"), method = "finishUsingItem")
    private void finishUsingItem(ItemStack itemStack, Level level, LivingEntity livingEntity, CallbackInfoReturnable<ItemStack> cir) {
        Float thirst = zomboid_health_system$getThirst();
        if(thirst != null && !level.isClientSide() && livingEntity instanceof PlayerAccess playerAccess) {
            Health health = playerAccess.zomboid_health_system$getModHealth();
            health.getThirst().drink(thirst, true);
        }
    }
}
