package aiven.zomboidhealthsystem.foundation.mixin;

import aiven.zomboidhealthsystem.Config;
import aiven.zomboidhealthsystem.foundation.player.moodles.Temperature;
import aiven.zomboidhealthsystem.foundation.world.ModServer;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CampfireBlockEntity.class)
public class CampfireBlockEntityMixin {
    @Inject(at = @At("HEAD"),method = "litServerTick")
    private static void tick(World world, BlockPos pos, BlockState state, CampfireBlockEntity campfire, CallbackInfo ci){
        for(PlayerEntity player : world.getPlayers()){
            float distance = (float) player.getPos().distanceTo(pos.toCenterPos());
            if(distance < 5){
                Temperature temperature = ModServer.getHealth(player).getTemperature();
                temperature.setHeatFromCampfire(Math.max(Config.MAX_HEAT_FROM_BLOCK.getValue() - (distance * Config.MAX_HEAT_FROM_BLOCK.getValue() / 5), temperature.getHeatFromCampfire()));
            }
        }
    }
}
