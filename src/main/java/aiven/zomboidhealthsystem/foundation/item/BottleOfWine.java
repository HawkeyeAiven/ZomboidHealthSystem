package aiven.zomboidhealthsystem.foundation.item;

import aiven.zomboidhealthsystem.ModFoodComponents;
import aiven.zomboidhealthsystem.foundation.player.Health;
import aiven.zomboidhealthsystem.foundation.world.ModServer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BottleOfWine extends Item {
    public BottleOfWine() {
        super(new FabricItemSettings().maxCount(1).food(ModFoodComponents.BOTTLE_OF_WINE));
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if(!world.isClient && user instanceof ServerPlayerEntity player) {
            Health health = ModServer.getHealth(player);
            if(health != null) {
                health.getDrunkenness().addAmount(1.75F);
            }
        }
        user.eatFood(world, stack);
        return Items.GLASS_BOTTLE.getDefaultStack();
    }

    @Override
    public SoundEvent getEatSound() {
        return SoundEvents.ENTITY_GENERIC_DRINK;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("zomboidhealthsystem.tooltip.allows_sleep_without_drowsiness").formatted(Formatting.GOLD));
        tooltip.add(Text.translatable("zomboidhealthsystem.tooltip.reduces_pain").formatted(Formatting.GOLD));
    }
}
