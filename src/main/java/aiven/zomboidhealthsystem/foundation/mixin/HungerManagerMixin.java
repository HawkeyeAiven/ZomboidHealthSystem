package aiven.zomboidhealthsystem.foundation.mixin;

import aiven.zomboidhealthsystem.foundation.world.ModServer;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = HungerManager.class, priority = 10000)
public class HungerManagerMixin {
    private PlayerEntity player;

    /**
     * @author Aiven
     * @reason No
     */
    @Overwrite
    public void update(PlayerEntity player) {
        this.player = player;
    }

    /**
     * @author Aiven
     * @reason Yeah.
     */
    @Overwrite
    public void add(int food, float saturationModifier) {
        if(player != null && !player.getWorld().isClient()) {
            ModServer.getHealth(player).getHunger().eatFood(food, saturationModifier);
        }
    }
}
