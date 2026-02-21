package aiven.zomboidhealthsystem.foundation.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.GameRules;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.function.Consumer;

@Mixin(value = World.class, priority = 10000)
public abstract class WorldMixin {
    @Shadow public abstract long getTimeOfDay();

    @Shadow protected abstract void tickBlockEntities();

    @Shadow public abstract <T extends Entity> void tickEntity(Consumer<T> tickConsumer, T entity);

    @Shadow public abstract long getTime();

    @Shadow public abstract boolean isDebugWorld();

    @Shadow public abstract void calculateAmbientDarkness();

    @Shadow @Final protected MutableWorldProperties properties;

    @Shadow public abstract GameRules getGameRules();

    @Shadow public abstract WorldBorder getWorldBorder();

    @Shadow public abstract Profiler getProfiler();

    @Shadow public abstract DimensionType getDimension();

    @Shadow private int ambientDarkness;

    @Shadow @Final public boolean isClient;

    /**
     * @author
     * @reason
     */
    @Overwrite
    public boolean isDay() {
        if(this.isClient) {
            return !this.getDimension().hasFixedTime() && this.ambientDarkness < 4;
        } else {
            return false;
        }
    }
}
