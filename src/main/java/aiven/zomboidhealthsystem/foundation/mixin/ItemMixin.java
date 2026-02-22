package aiven.zomboidhealthsystem.foundation.mixin;

import aiven.zomboidhealthsystem.foundation.player.moodles.Hunger;
import aiven.zomboidhealthsystem.foundation.utility.ItemUtil;
import aiven.zomboidhealthsystem.foundation.utility.Util;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Item.class)
public abstract class ItemMixin {
    @Shadow public abstract boolean isFood();


    @Inject(at = @At("TAIL"), method = "appendTooltip")
    private void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context, CallbackInfo ci) {
        if(isFood()) {
            String hunger = Text.translatable("zomboidhealthsystem.tooltip.hunger").getString();
            tooltip.add(Text.literal(hunger + ": " + Util.floor((float) stack.getItem().getFoodComponent().getHunger() * Hunger.DEFAULT_APPETITE, 100)).formatted(Formatting.GOLD));
        }

        Item item = stack.getItem();
        float amount = ItemUtil.getThirst(item);
        if(amount != -1) {
            String thirst = Text.translatable("zomboidhealthsystem.tooltip.thirst").getString();
            tooltip.add(Text.literal(thirst + ": " + amount).formatted(Formatting.GOLD));
        }
    }

    @Unique
    private Item toItem() {
        return (Item) ((Object) this);
    }
}
