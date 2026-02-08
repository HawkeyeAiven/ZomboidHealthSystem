package aiven.zomboidhealthsystem.foundation.items;

import aiven.zomboidhealthsystem.ModFoodComponents;
import aiven.zomboidhealthsystem.ModItems;
import aiven.zomboidhealthsystem.foundation.world.ModServer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

public class Coffee extends Item {
    public Coffee() {
        super(new FabricItemSettings().maxCount(1).food(ModFoodComponents.COFFEE));
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (user instanceof PlayerEntity player && !world.isClient && !player.isCreative()) {
            ModServer.getHealth(player).getDrowsiness().addCaffeine(1.0F);
            ModServer.getHealth(player).getThirst().drink(1.0F, true);
        }
        return ModItems.EMPTY_CUP.getDefaultStack();
    }

    @Override
    public SoundEvent getEatSound() {
        return SoundEvents.ITEM_HONEY_BOTTLE_DRINK;
    }
}
