package aiven.zomboidhealthsystem.mixin.mixin;

import aiven.zomboidhealthsystem.ZomboidHealthSystem;
import aiven.zomboidhealthsystem.mixin.access.PrimaryLevelDataAccess;

import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.Lifecycle;

import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.storage.LevelVersion;
import net.minecraft.world.level.storage.PrimaryLevelData;

import org.jetbrains.annotations.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = PrimaryLevelData.class)
public class PrimaryLevelDataMixin implements PrimaryLevelDataAccess {

    @Unique
    private static final String zomboidhealthsystem$windId = ZomboidHealthSystem.ID + ".wind";

    @Unique
    private static final String zomboidhealthsystem$rainTemperatureId = ZomboidHealthSystem.ID + ".rain_temperature";

    @Unique
    private float zomboidhealthsystem$wind = 0.0F;

    @Unique
    private float zomboidhealthsystem$rainTemperature = 0.0F;

    @Override
    public float zomboid_health_system$getWind() {
        return zomboidhealthsystem$wind;
    }

    @Override
    public void zomboid_health_system$setWind(float value) {
        this.zomboidhealthsystem$wind = value;
    }

    @Override
    public float zomboidhealthsystem$getRainTemperature() {
        return zomboidhealthsystem$rainTemperature;
    }

    @Override
    public void zomboidhealthsystem$setRainTemperature(float rainTemperature) {
        this.zomboidhealthsystem$rainTemperature = rainTemperature;
    }

    @Inject(at = @At("RETURN"), method = "setTagData")
    private void setTagData(RegistryAccess registryAccess, CompoundTag compoundTag, CompoundTag compoundTag2, CallbackInfo ci) {
        compoundTag.putFloat(zomboidhealthsystem$windId, zomboidhealthsystem$wind);
        compoundTag.putFloat(zomboidhealthsystem$rainTemperatureId, zomboidhealthsystem$rainTemperature);
    }

    @Inject(at = @At("RETURN"), method = "parse")
    private static void parse(Dynamic<?> dynamic, DataFixer dataFixer, int i, @Nullable CompoundTag compoundTag, LevelSettings levelSettings, LevelVersion levelVersion, PrimaryLevelData.SpecialWorldProperty specialWorldProperty, WorldOptions worldOptions, Lifecycle lifecycle, CallbackInfoReturnable<PrimaryLevelData> cir) {
        float wind = dynamic.get(zomboidhealthsystem$windId).asFloat(0.0F);
        ((PrimaryLevelDataAccess) cir.getReturnValue()).zomboid_health_system$setWind(wind);

        float rainTemperature = dynamic.get(zomboidhealthsystem$rainTemperatureId).asFloat(0.0F);
        ((PrimaryLevelDataAccess) cir.getReturnValue()).zomboidhealthsystem$setRainTemperature(rainTemperature);
    }
}
