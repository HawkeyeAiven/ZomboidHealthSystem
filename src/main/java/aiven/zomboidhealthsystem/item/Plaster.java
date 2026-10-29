package aiven.zomboidhealthsystem.item;


import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Plaster extends AbstractBandageItem {
    @Override
    public AbstractBandageItem getDirtyBandageItem() {
        return null;
    }

    @Override
    public float getHealAmount() {
        return 1;
    }

    @Override
    public boolean isDirty() {
        return false;
    }

    @Override
    public boolean isStopBleeding() {
        return false;
    }

    @Override
    public AbstractBandageItem getCleanBandageItem() {
        return null;
    }


    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        list.add(Component.translatable("zomboid-health-system.tooltip.doesnt_stop_bleeding").withStyle(ChatFormatting.GOLD));
    }
}
