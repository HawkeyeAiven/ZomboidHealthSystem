package aiven.zomboidhealthsystem.foundation.item;

import aiven.zomboidhealthsystem.foundation.player.Health;
import aiven.zomboidhealthsystem.foundation.world.ModServer;
import aiven.zomboidhealthsystem.foundation.utility.Util;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Thermometer extends Item {
    public Thermometer() {
        super(new FabricItemSettings().maxCount(1));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if(!world.isClient){
            float temperature = (float) Util.floor(Util.toDouble(ModServer.getHealth(user).getTemperature().getAmount()), 10);
            float perceivedTemperature = (float) Util.floor(Util.toDouble(getPerceivedTemperature(user)), 10);
            user.sendMessage(Text.of("""
                    body t: %s°C, perceived t: %s°C\
                    """.formatted(temperature, perceivedTemperature)),true);
        }
        return TypedActionResult.success(user.getMainHandStack());
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("zomboidhealthsystem.tooltip.shows_temp_thermometer").formatted(Formatting.GOLD));
    }

    public float getPerceivedTemperature(PlayerEntity player) {
        Health health = ModServer.getHealth(player);
        return health.getTemperature().getPerceivedTemperature();
    }
}
