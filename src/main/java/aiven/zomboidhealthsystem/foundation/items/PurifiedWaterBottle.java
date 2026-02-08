package aiven.zomboidhealthsystem.foundation.items;

import aiven.zomboidhealthsystem.ModFoodComponents;
import aiven.zomboidhealthsystem.foundation.world.ModServer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

public class PurifiedWaterBottle extends Item {
    public PurifiedWaterBottle() {
        super(new FabricItemSettings().maxCount(1).food(ModFoodComponents.BOTTLE_OF_WATER));
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (!world.isClient && user instanceof PlayerEntity player) {
            ModServer.getHealth(player).getThirst().drink(1.25F, true);
        }

        return Items.GLASS_BOTTLE.getDefaultStack();
    }

    @Override
    public SoundEvent getEatSound() {
        return SoundEvents.ENTITY_GENERIC_DRINK;
    }
}
