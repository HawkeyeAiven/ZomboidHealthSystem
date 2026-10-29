package aiven.zomboidhealthsystem.item;

import aiven.zomboidhealthsystem.ZomboidHealthSystem;
import aiven.zomboidhealthsystem.mixin.access.PlayerAccess;
import aiven.zomboidhealthsystem.health.Health;
import aiven.zomboidhealthsystem.utility.Util;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;

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

public class Thermometer extends Item {

    public Thermometer() {
        super(new FabricItemSettings().maxCount(1));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        if(!level.isClientSide && player instanceof PlayerAccess playerAccess) {
            Health health = playerAccess.zomboid_health_system$getModHealth();
            float temperature = Util.floor(health.getTemperature().getAmount(), 10);
            float perceived = Util.floor(health.getTemperature().getPerceivedTemperature(), 10);

            player.sendSystemMessage(Component.literal("""
                    body t: %s°C, perceived t: %s°C\
                    """.formatted(temperature, perceived)));
        }
        return InteractionResultHolder.success(player.getItemInHand(interactionHand));
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        list.add(Component.translatable(ZomboidHealthSystem.ID + ".tooltip.shows_temp_thermometer").withStyle(ChatFormatting.GOLD));
    }
}
