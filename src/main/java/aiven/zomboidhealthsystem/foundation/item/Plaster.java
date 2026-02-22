package aiven.zomboidhealthsystem.foundation.item;


import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Plaster extends BandageItem {
    @Override
    public BandageItem getDirtyBandageItem() {
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
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("zomboidhealthsystem.tooltip.doesnt_stop_bleeding").formatted(Formatting.GOLD));
    }
}
