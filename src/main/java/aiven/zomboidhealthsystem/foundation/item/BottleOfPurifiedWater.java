package aiven.zomboidhealthsystem.foundation.item;

import aiven.zomboidhealthsystem.ModFoodComponents;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

public class BottleOfPurifiedWater extends Item {
    public BottleOfPurifiedWater() {
        super(new FabricItemSettings().maxCount(1).food(ModFoodComponents.BOTTLE_OF_WATER));
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        user.eatFood(world, stack);
        return Items.GLASS_BOTTLE.getDefaultStack();
    }

    @Override
    public SoundEvent getEatSound() {
        return SoundEvents.ENTITY_GENERIC_DRINK;
    }
}
