package aiven.zomboidhealthsystem.foundation.item;

import aiven.zomboidhealthsystem.ModFoodComponents;
import aiven.zomboidhealthsystem.ModItems;
import aiven.zomboidhealthsystem.foundation.world.ModServer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CupOfCoffee extends Item {
    public CupOfCoffee() {
        super(new FabricItemSettings().maxCount(1).food(ModFoodComponents.COFFEE));
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (user instanceof PlayerEntity player && !world.isClient && !player.isCreative()) {
            ModServer.getHealth(player).getDrowsiness().addCaffeine(1.0F);
        }
        user.eatFood(world, stack);
        return ModItems.EMPTY_CUP.getDefaultStack();
    }

    @Override
    public SoundEvent getEatSound() {
        return SoundEvents.ITEM_HONEY_BOTTLE_DRINK;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("zomboidhealthsystem.tooltip.works_over_time").formatted(Formatting.GOLD));
    }
}
