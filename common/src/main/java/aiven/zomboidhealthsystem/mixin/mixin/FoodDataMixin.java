package aiven.zomboidhealthsystem.mixin.mixin;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = FoodData.class, priority = 10000)
public abstract class FoodDataMixin {

    /**
     * @author Aiven
     * @reason Долой казуальщину!
     */
    @Overwrite
    public void tick(Player player) {
    }
}
