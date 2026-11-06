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
    private static final String windId = ZomboidHealthSystem.ID + ".wind";

    @Unique
    private float wind = 0.0F;

    @Override
    public float zomboid_health_system$getWind() {
        return wind;
    }

    @Override
    public void zomboid_health_system$setWind(float value) {
        this.wind = value;
    }

    @Inject(at = @At("RETURN"), method = "setTagData")
    private void setTagData(RegistryAccess registryAccess, CompoundTag compoundTag, CompoundTag compoundTag2, CallbackInfo ci) {
        compoundTag.putFloat(windId, wind);
    }


    @Inject(at = @At("RETURN"), method = "parse")
    private static void parse(Dynamic<?> dynamic, DataFixer dataFixer, int i, @Nullable CompoundTag compoundTag, LevelSettings levelSettings, LevelVersion levelVersion, PrimaryLevelData.SpecialWorldProperty specialWorldProperty, WorldOptions worldOptions, Lifecycle lifecycle, CallbackInfoReturnable<PrimaryLevelData> cir) {
        float wind = dynamic.get(windId).asFloat(0.0F);
        ((PrimaryLevelDataAccess) cir.getReturnValue()).zomboid_health_system$setWind(wind);
    }
}
