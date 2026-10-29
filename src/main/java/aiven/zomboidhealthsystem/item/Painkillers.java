package aiven.zomboidhealthsystem.item;

import aiven.zomboidhealthsystem.mixin.access.PlayerAccess;
import aiven.zomboidhealthsystem.health.Health;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Painkillers extends Item {

    private static final Item.Properties itemProperties;
    static {
        itemProperties = new Properties();
        itemProperties.stacksTo(1);
        itemProperties.durability(10);
    }

    public Painkillers() {
        super(itemProperties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        super.use(level, player, interactionHand);

        ItemStack itemStack = player.getItemInHand(interactionHand);
        itemStack.setDamageValue(itemStack.getDamageValue() + 1);
        if(itemStack.getDamageValue() > 10) {
            itemStack.setCount(itemStack.getCount() - 1);
        }

        player.getCooldowns().addCooldown(this, 20);

        if(!level.isClientSide && player instanceof PlayerAccess playerAccess) {
            Health health = playerAccess.zomboid_health_system$getModHealth();
            health.getPain().addPainkiller(1.0F);
        }

        return InteractionResultHolder.success(itemStack);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        list.add(Component.translatable("zomboid-health-system.tooltip.reduces_pain_by_50").withStyle(ChatFormatting.GOLD));
        list.add(Component.translatable("zomboid-health-system.tooltip.works_over_time").withStyle(ChatFormatting.GOLD));
    }
}
