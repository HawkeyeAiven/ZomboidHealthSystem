package aiven.zomboidhealthsystem.mixin.mixin;

import aiven.zomboidhealthsystem.Config;
import aiven.zomboidhealthsystem.mixin.access.PlayerAccess;
import aiven.zomboidhealthsystem.mixin.access.PrimaryLevelDataAccess;
import aiven.zomboidhealthsystem.mixin.access.ServerLevelAccess;
import aiven.zomboidhealthsystem.health.Health;
import aiven.zomboidhealthsystem.utility.Util;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.SleepStatus;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.GameRules;

import org.jetbrains.annotations.Nullable;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Random;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

@Mixin(value = ServerLevel.class, priority = 10000)
public abstract class ServerLevelMixin extends LevelMixin implements ServerLevelAccess {


    @Shadow
    public abstract List<ServerPlayer> getPlayers(Predicate<? super ServerPlayer> predicate);

    @Shadow
    @Final
    private SleepStatus sleepStatus;

    @Shadow
    @Final
    List<ServerPlayer> players;

    @Shadow
    @Final
    private MinecraftServer server;
    @Unique
    private @Nullable List<ServerPlayer> zomboidhealthsystem$lastSleepingPlayers = null;

    @Unique
    private @Nullable Long zomboidhealthsystem$lastDayTime = null;

    @Override
    public float zomboid_health_system$getWind() {
        if(levelData instanceof PrimaryLevelDataAccess access) {
            return access.zomboid_health_system$getWind();
        } else {
            return 0.0F;
        }
    }

    @Override
    public float zomboidhealthsystem$getRainTemperature() {
        if(levelData instanceof PrimaryLevelDataAccess access) {
            return access.zomboidhealthsystem$getRainTemperature();
        } else {
            return 0.0F;
        }
    }

    @Unique
    private void zomboidhealthsystem$setWind(float value) {
        if(levelData instanceof PrimaryLevelDataAccess access) {
            access.zomboid_health_system$setWind(value);
        }
    }

    @Unique
    private void zomboidhealthsystem$setRainTemperature(float value) {
        if(levelData instanceof PrimaryLevelDataAccess access) {
            access.zomboidhealthsystem$setRainTemperature(value);
        }
    }

    @Inject(at = @At("HEAD"), method = "tick")
    public void beginTick(BooleanSupplier booleanSupplier, CallbackInfo ci) {
        int i = this.getGameRules().getInt(GameRules.RULE_PLAYERS_SLEEPING_PERCENTAGE);
        if (this.sleepStatus.areEnoughSleeping(i) && this.sleepStatus.areEnoughDeepSleeping(i, this.players)) {
            if (this.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)) {
                zomboidhealthsystem$lastSleepingPlayers = getPlayers((LivingEntity::isSleeping));
                zomboidhealthsystem$lastDayTime = getDayTime();
            }
        }
    }

    @Inject(at = @At("RETURN"), method = "tick")
    public void endTick(BooleanSupplier booleanSupplier, CallbackInfo ci) {
        if(zomboidhealthsystem$lastSleepingPlayers != null && zomboidhealthsystem$lastDayTime != null) {
            long ticks = getDayTime() - zomboidhealthsystem$lastDayTime;
            zomboidhealthsystem$onSleep(zomboidhealthsystem$lastSleepingPlayers, ticks);
            zomboidhealthsystem$lastSleepingPlayers = null;
            zomboidhealthsystem$lastDayTime = null;
        }

        zomboidhealthsystem$updateWind(1);

        {
            float m;
            if(isRaining()) {
                m = -1;
            } else {
                m = 1;
            }

            float rainTemp = zomboidhealthsystem$getRainTemperature();
            float d = 1.0F * Config.getInstance().rain_temperature_speed;
            float min = Config.getInstance().max_rain_temperature_shift * -1;
            zomboidhealthsystem$setRainTemperature(Util.clamp(rainTemp + (d * m), min, 0));
        }
    }

    @Unique
    private void zomboidhealthsystem$updateWind(int tickDelta) {
        if (Util.random(Config.getInstance().wind_update_chance * tickDelta)) {
            if (Util.random(Config.getInstance().wind_chance)) {
                zomboidhealthsystem$setWind(new Random().nextFloat(Config.getInstance().min_wind, Config.getInstance().max_wind));
            } else {
                zomboidhealthsystem$setWind(0.0F);
            }
        }
    }

    @Unique
    private void zomboidhealthsystem$onSleep(List<ServerPlayer> players, long time) {
        zomboidhealthsystem$updateWind(Math.toIntExact(time));
        for(ServerPlayer player : players) {
            Health health = ((PlayerAccess) player).zomboid_health_system$getModHealth();
            health.sleep(Math.toIntExact(time));
            health.onSleep(Math.toIntExact(time));
        }
    }
}
