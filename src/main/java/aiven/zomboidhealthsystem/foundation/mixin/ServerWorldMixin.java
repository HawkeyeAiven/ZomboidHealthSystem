package aiven.zomboidhealthsystem.foundation.mixin;

import aiven.zomboidhealthsystem.Config;
import aiven.zomboidhealthsystem.foundation.world.ModServer;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.dragon.EnderDragonFight;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerEntityManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.SleepManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.village.raid.RaidManager;
import net.minecraft.world.EntityList;
import net.minecraft.world.GameRules;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.tick.WorldTickScheduler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;

import java.util.List;
import java.util.function.BooleanSupplier;

@Mixin(value = ServerWorld.class, priority = 500)
public abstract class ServerWorldMixin extends WorldMixin {

    @Shadow @Final private boolean shouldTickTime;

    @Shadow @Final private ServerWorldProperties worldProperties;

    @Shadow public abstract void setTimeOfDay(long timeOfDay);

    @Shadow @Final private MinecraftServer server;

    @Shadow @NotNull public abstract MinecraftServer getServer();

    @Shadow public abstract String toString();

    @Shadow private boolean inBlockTick;

    @Shadow protected abstract void tickWeather();

    @Shadow @Final private SleepManager sleepManager;

    @Shadow @Final
    List<ServerPlayerEntity> players;

    @Shadow protected abstract void wakeSleepingPlayers();

    @Shadow @Final private WorldTickScheduler<Block> blockTickScheduler;

    @Shadow protected abstract void tickBlock(BlockPos pos, Block block);

    @Shadow @Final private WorldTickScheduler<Fluid> fluidTickScheduler;

    @Shadow protected abstract void tickFluid(BlockPos pos, Fluid fluid);

    @Shadow @Final protected RaidManager raidManager;

    @Shadow public abstract ServerChunkManager getChunkManager();

    @Shadow protected abstract void processSyncedBlockEvents();

    @Shadow public abstract LongSet getForcedChunks();

    @Shadow public abstract void resetIdleTimeout();

    @Shadow private int idleTimeout;

    @Shadow @Nullable private EnderDragonFight enderDragonFight;

    @Shadow @Final
    EntityList entityList;

    @Shadow protected abstract boolean shouldCancelSpawn(Entity entity);

    @Shadow @Final private ServerChunkManager chunkManager;

    @Shadow public abstract void tickEntity(Entity entity);

    @Shadow @Final private ServerEntityManager<Entity> entityManager;

    @Unique private int sleepTicks = 0;

    /**
     * @author Aiven
     * @reason G
     */
    @Overwrite
    public void tick(BooleanSupplier shouldKeepTicking) {
        Profiler profiler = this.getProfiler();
        this.inBlockTick = true;
        profiler.push("world border");
        this.getWorldBorder().tick();
        profiler.swap("weather");
        this.tickWeather();
        int i = this.getGameRules().getInt(GameRules.PLAYERS_SLEEPING_PERCENTAGE);
        if (this.sleepManager.canSkipNight(i) && this.sleepManager.canResetTime(i, this.players)) {
            if (this.getGameRules().getBoolean(GameRules.DO_DAYLIGHT_CYCLE)) {
                this.setTimeOfDay(getTimeOfDay() + Config.SLEEP_SPEED.getValue());
                sleepTicks += Config.SLEEP_SPEED.getValue();
            }

            for(PlayerEntity player : this.players) {
                if(player.isSleeping()) {
                    ModServer.getHealth(player).sleep(Config.SLEEP_SPEED.getValue());
                }
            }

            if(sleepTicks >= 12000) {
                this.wakeSleepingPlayers();
                sleepTicks = 0;
            }
        } else {
            sleepTicks = 0;
        }

        this.calculateAmbientDarkness();
        this.tickTime();
        profiler.swap("tickPending");
        if (!this.isDebugWorld()) {
            long l = this.getTime();
            profiler.push("blockTicks");
            this.blockTickScheduler.tick(l, 65536, this::tickBlock);
            profiler.swap("fluidTicks");
            this.fluidTickScheduler.tick(l, 65536, this::tickFluid);
            profiler.pop();
        }

        profiler.swap("raid");
        this.raidManager.tick();
        profiler.swap("chunkSource");
        this.getChunkManager().tick(shouldKeepTicking, true);
        profiler.swap("blockEvents");
        this.processSyncedBlockEvents();
        this.inBlockTick = false;
        profiler.pop();
        boolean bl = !this.players.isEmpty() || !this.getForcedChunks().isEmpty();
        if (bl) {
            this.resetIdleTimeout();
        }

        if (bl || this.idleTimeout++ < 300) {
            profiler.push("entities");
            if (this.enderDragonFight != null) {
                profiler.push("dragonFight");
                this.enderDragonFight.tick();
                profiler.pop();
            }

            this.entityList.forEach((entity) -> {
                if (!entity.isRemoved()) {
                    if (this.shouldCancelSpawn(entity)) {
                        entity.discard();
                    } else {
                        profiler.push("checkDespawn");
                        entity.checkDespawn();
                        profiler.pop();
                        if (this.chunkManager.threadedAnvilChunkStorage.getTicketManager().shouldTickEntities(entity.getChunkPos().toLong())) {
                            Entity entity2 = entity.getVehicle();
                            if (entity2 != null) {
                                if (!entity2.isRemoved() && entity2.hasPassenger(entity)) {
                                    return;
                                }

                                entity.stopRiding();
                            }

                            profiler.push("tick");
                            this.tickEntity(this::tickEntity, entity);
                            profiler.pop();
                        }
                    }
                }
            });
            profiler.pop();
            this.tickBlockEntities();
        }

        profiler.push("entityManagement");
        this.entityManager.tick();
        profiler.pop();
    }


    @Unique
    private int i = 0;

    /**
     * @author Aiven
     * @reason The Minecraft day duration is very short
     */
    @Overwrite
    public void tickTime()  {
        int dayLengthMultiplier = ModServer.WORLD_SETTINGS.getDayLengthMultiplier();
        long d = 0;
        if(dayLengthMultiplier > 0 && i >= dayLengthMultiplier - 1) {
            d = 1;
            i = 0;
        }
        i++;
        if (this.shouldTickTime) {
            long l = this.worldProperties.getTime() + 1;
            this.worldProperties.setTime(l);
            this.worldProperties.getScheduledEvents().processEvents(this.server, l);
            if (this.worldProperties.getGameRules().getBoolean(GameRules.DO_DAYLIGHT_CYCLE)) {
                this.setTimeOfDay(this.worldProperties.getTimeOfDay() + d);
            }
        }
    }
}