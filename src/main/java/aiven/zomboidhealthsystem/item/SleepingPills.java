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

public class SleepingPills extends Item {

    private static final Item.Properties itemProperties;
    static {
        itemProperties = new Properties();
        itemProperties.stacksTo(1);
        itemProperties.durability(12);
    }

    public SleepingPills() {
        super(itemProperties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        super.use(level, player, interactionHand);

        ItemStack itemStack = player.getItemInHand(interactionHand);

        if(!level.isClientSide()) {
            player.getCooldowns().addCooldown(this, 20);

            itemStack.setDamageValue(itemStack.getDamageValue() - 1);

            if(itemStack.getDamageValue() > itemStack.getMaxDamage()) {
                itemStack.setCount(itemStack.getCount() - 1);
            }

            Health health = ((PlayerAccess) player).zomboid_health_system$getModHealth();
            health.getDrowsiness().addSleepingPills(1.0F);
        }

        return InteractionResultHolder.success(itemStack);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        list.add(Component.translatable("zomboid-health-system.tooltip.works_over_time").withStyle(ChatFormatting.GOLD));
    }
}
