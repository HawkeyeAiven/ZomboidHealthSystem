package aiven.zomboidhealthsystem.foundation.item;

import aiven.zomboidhealthsystem.foundation.player.Health;
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

public class SleepingPills extends ToolItem {
    public SleepingPills() {
        super(new ToolMaterial() {
            @Override
            public int getDurability() {
                return 12;
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
                return Ingredient.EMPTY;
            }
        }, new FabricItemSettings());
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if(!world.isClient()) {
            ItemStack stack = user.getStackInHand(hand);

            user.getItemCooldownManager().set(stack.getItem(), 20);

            stack.setDamage(stack.getDamage() + 1);

            if(stack.getDamage() > stack.getMaxDamage()) {
                stack.decrement(1);
            }

            Health health = ModServer.getHealth(user);

            health.getDrowsiness().addSleepingPills(1.0F);

            return TypedActionResult.success(stack, true);
        }
        return super.use(world, user, hand);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("zomboidhealthsystem.tooltip.works_over_time").formatted(Formatting.GOLD));
    }
}
