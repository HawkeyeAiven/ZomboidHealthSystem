package aiven.zomboidhealthsystem.foundation.mixin;

import aiven.zomboidhealthsystem.Config;
import aiven.zomboidhealthsystem.foundation.world.ModServer;
import aiven.zomboidhealthsystem.foundation.world.Weather;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import net.minecraft.world.WorldView;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Biome.class)
public abstract class BiomeMixin {
    @Shadow
    protected abstract float getTemperature(BlockPos blockPos);

    @Unique
    private Float seasonTemperature;

    /**
     * @author
     * @reason
     */
    @Overwrite
    public boolean doesNotSnow(BlockPos pos) {
        if(seasonTemperature == null) {
            seasonTemperature = Weather.getSeasonTemperature(ModServer.WORLD_SETTINGS_ON_CREATING_WORLD, null);
        }

        if (ModServer.WEATHER != null && ModServer.WORLD_SETTINGS.hasTemperature()) {
            return ModServer.WEATHER.doesNotSnow(pos, (Biome) ((Object) this));
        } else if (ModServer.WORLD_SETTINGS_ON_CREATING_WORLD.hasTemperature()) {
            return this.getTemperature(pos) >= 0.15F && seasonTemperature > 0;
        } else {
            return this.getTemperature(pos) >= 0.15F;
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public boolean canSetIce(WorldView world, BlockPos pos, boolean doWaterCheck) {
        if (this.doesNotSnow(pos)) {
            return false;
        } else if (!Config.FROZEN_OCEANS_IN_WINTER.getValue() && (world.getBiome(pos).isIn(BiomeTags.IS_OCEAN) || world.getBiome(pos).isIn(BiomeTags.IS_DEEP_OCEAN))) {
            return false;
        } else {
            if (pos.getY() >= world.getBottomY() && pos.getY() < world.getTopY() && world.getLightLevel(LightType.BLOCK, pos) < 10) {
                BlockState blockState = world.getBlockState(pos);
                FluidState fluidState = world.getFluidState(pos);
                if (fluidState.getFluid() == Fluids.WATER && blockState.getBlock() instanceof FluidBlock) {
                    if (!doWaterCheck) {
                        return true;
                    }

                    boolean bl = world.isWater(pos.west()) && world.isWater(pos.east()) && world.isWater(pos.north()) && world.isWater(pos.south());
                    if (!bl) {
                        return true;
                    }
                }
            }
            return false;
        }
    }
}
