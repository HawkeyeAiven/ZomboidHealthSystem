package aiven.zomboidhealthsystem.mixin.mixin;

import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = Level.class)
public abstract class LevelMixin {
    @Shadow
    public abstract long getDayTime();

    @Shadow
    public abstract GameRules getGameRules();
}
