package aiven.zomboidhealthsystem.foundation.mixin;

import aiven.zomboidhealthsystem.Config;
import aiven.zomboidhealthsystem.foundation.player.moodles.Temperature;
import aiven.zomboidhealthsystem.foundation.world.ModServer;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Enumeration;

@Mixin(AbstractFurnaceBlockEntity.class)
public class AbstractFurnaceBlockEntityMixin {

    @Shadow
    int burnTime;

    @Unique
    private BlockPos pos;

    @Inject(at = @At("CTOR_HEAD"), method = "<init>")
    private void init(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state, RecipeType<?> recipeType, CallbackInfo ci){
        this.pos = pos;
    }

    @Inject(at = @At("HEAD"), method = "isBurning")
    private void isBurning(CallbackInfoReturnable<Boolean> cir){
        try {
            if (this.burnTime > 0) {
                Enumeration<PlayerEntity> list = ModServer.getRegisteredPlayers();
                while (list.hasMoreElements()) {
                    PlayerEntity player = list.nextElement();
                    float distance = (float) player.getPos().distanceTo(this.pos.toCenterPos());
                    if (distance < 5) {
                        Temperature temperature = ModServer.getHealth(player).getTemperature();
                        temperature.setHeatFromFurnace(Math.max(Config.MAX_HEAT_FROM_BLOCK.getValue() - (distance * Config.MAX_HEAT_FROM_BLOCK.getValue()), temperature.getHeatFromFurnace()));
                    }
                }
            }
        } catch (NullPointerException ignored){

        }
    }
}
