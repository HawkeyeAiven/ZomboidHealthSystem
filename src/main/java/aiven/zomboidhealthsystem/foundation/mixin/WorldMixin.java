package aiven.zomboidhealthsystem.foundation.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
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

    @Shadow public abstract GameRules getGameRules();

    @Shadow public abstract WorldBorder getWorldBorder();

    @Shadow public abstract Profiler getProfiler();

    /**
     * @author
     * @reason
     */
    @Overwrite
    public boolean isDay() {
        return false;
    }
}
