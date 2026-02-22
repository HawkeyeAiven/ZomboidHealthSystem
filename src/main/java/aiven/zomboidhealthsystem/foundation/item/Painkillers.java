package aiven.zomboidhealthsystem.foundation.item;


import aiven.zomboidhealthsystem.foundation.world.ModServer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Painkillers extends ToolItem {
    public Painkillers() {
        super(new ToolMaterial() {
            @Override
            public int getDurability() {
                return 10;
            }

            @Override
            public float getMiningSpeedMultiplier() {
                return 0;
            }

            @Override
            public float getAttackDamage() {
                return 0;
            }

            @Override
            public int getMiningLevel() {
                return 0;
            }

            @Override
            public int getEnchantability() {
                return 0;
            }

            @Override
            public Ingredient getRepairIngredient() {
                return null;
            }
        },new FabricItemSettings());
    }


    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("zomboidhealthsystem.tooltip.reduces_pain").formatted(Formatting.GOLD));
        tooltip.add(Text.translatable("zomboidhealthsystem.tooltip.works_over_time").formatted(Formatting.GOLD));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getMainHandStack();
        stack.setDamage(stack.getDamage() + 1);
        if(stack.getDamage() > 10){
            stack.decrement(1);
        }
        user.getItemCooldownManager().set(user.getMainHandStack().getItem(), 20);
        if(!world.isClient) {
            ModServer.getHealth(user).getPain().addPainkiller(1.0F);
        }
        return TypedActionResult.success(stack);
    }
}
