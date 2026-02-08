package aiven.zomboidhealthsystem.foundation.mixin;

import aiven.zomboidhealthsystem.Config;
import aiven.zomboidhealthsystem.foundation.world.ModServer;
import aiven.zomboidhealthsystem.foundation.world.Weather;
import aiven.zomboidhealthsystem.infrastructure.config.JsonConfig;
import aiven.zomboidhealthsystem.infrastructure.config.codecs.WeatherCodec;
import aiven.zomboidhealthsystem.infrastructure.config.codecs.WorldSettingsCodec;
import com.mojang.datafixers.DataFixer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.SaveLoader;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.WorldGenerationProgressListenerFactory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ApiServices;
import net.minecraft.util.WorldSavePath;
import net.minecraft.world.level.storage.LevelStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.io.IOException;
import java.net.Proxy;
import java.nio.file.Path;
import java.util.function.BooleanSupplier;

@Mixin(value = MinecraftServer.class, priority = 2000)
public abstract class MinecraftServerMixin {
    @Shadow public abstract Path getSavePath(WorldSavePath worldSavePath);

    @Shadow public abstract ServerWorld getOverworld();

    @Unique private File worldDateFile;

    @Unique private JsonConfig jsonConfig;

    @Unique private WorldSettingsCodec worldSettingsCodec;

    @Unique private WeatherCodec weatherCodec;

    @Unique private int ticks = 0;

    @Unique private int autosaveTicks = 0;


    @Inject(at = @At("TAIL"), method = "<init>")
    private void init(Thread serverThread, LevelStorage.Session session, ResourcePackManager dataPackManager, SaveLoader saveLoader, Proxy proxy, DataFixer dataFixer, ApiServices apiServices, WorldGenerationProgressListenerFactory worldGenerationProgressListenerFactory, CallbackInfo ci) {
        worldDateFile = new File(getPath(), "zomboidhealthsystem.json");
        worldSettingsCodec = new WorldSettingsCodec("world_settings", null);
        weatherCodec = new WeatherCodec("weather", null);

        jsonConfig = new JsonConfig();
        jsonConfig.add(worldSettingsCodec);
        jsonConfig.add(weatherCodec);
    }

    @Inject(at = @At("TAIL"), method = "createWorlds")
    private void createWorlds(WorldGenerationProgressListener worldGenerationProgressListener, CallbackInfo ci) throws IOException {

        if (worldDateFile.exists()) {
            jsonConfig.loadValues(worldDateFile);
            weatherCodec.getValue().setWorld(getOverworld());
            weatherCodec.getValue().setWorldSettings(worldSettingsCodec.getValue());
        } else {
            worldDateFile.createNewFile();
            worldSettingsCodec.setValue(ModServer.WORLD_SETTINGS_ON_CREATING_WORLD);
            weatherCodec.setValue(new Weather(getOverworld(), worldSettingsCodec.getValue()));
            weatherCodec.getValue().setWorldTemperature(weatherCodec.getValue().getSeasonTemperature());
        }

        ModServer.WEATHER = weatherCodec.getValue();
        ModServer.WORLD_SETTINGS = worldSettingsCodec.getValue();
    }

    @Inject(at = @At("TAIL"), method = "tick")
    private void tick(BooleanSupplier shouldKeepTicking, CallbackInfo ci) throws IOException {
        weatherCodec.getValue().tick();

        if(ticks++ >= 20 - 1) {
            for(PlayerEntity player : getOverworld().getPlayers()) {
                ModServer.sendPacketWorld(player);
            }
            ticks = 0;
        }

        if(autosaveTicks++ >= Config.AUTOSAVE_FREQUENCY.getValue()) {
            jsonConfig.save(worldDateFile);
            autosaveTicks = 0;
        }
    }

    @Inject(at = @At("TAIL"), method = "stop")
    private void stop(CallbackInfo ci) throws IOException {
        jsonConfig.save(worldDateFile);
        ModServer.WEATHER = null;
        ModServer.WORLD_SETTINGS = null;
    }


    @Unique
    private String getPath() {
        return getSavePath(WorldSavePath.LEVEL_DAT).toString().replace("\\level.dat", "");
    }
}
