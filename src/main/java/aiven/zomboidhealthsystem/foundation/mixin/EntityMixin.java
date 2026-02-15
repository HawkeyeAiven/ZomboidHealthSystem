package aiven.zomboidhealthsystem.foundation.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = Entity.class, priority = 10000)
public abstract class EntityMixin {

    @Shadow public abstract Text getName();

    @Shadow public abstract boolean isPlayer();

    @Shadow public abstract boolean isCrawling();

    @Shadow public abstract boolean hasVehicle();

    @Shadow public abstract boolean isSneaking();

    @Shadow protected abstract boolean wouldPoseNotCollide(EntityPose pose);

    @Shadow public boolean velocityDirty;

    @Shadow public abstract float getYaw();

    @Shadow public abstract void setVelocity(double x, double y, double z);

    @Shadow public abstract void setVelocity(Vec3d velocity);

    @Shadow public abstract Vec3d getVelocity();

    @Shadow public abstract boolean isSprinting();

    @Shadow @Nullable public abstract MinecraftServer getServer();

    @Shadow public abstract double getZ();

    @Shadow public abstract double getX();

    @Shadow protected abstract void scheduleVelocityUpdate();

    @Shadow public int timeUntilRegen;

    @Shadow public abstract EntityType<?> getType();

    @Shadow
    public abstract World getWorld();

    @Shadow @Final protected DataTracker dataTracker;

    @Shadow @Final protected static TrackedData<EntityPose> POSE;

    @Shadow public abstract EntityPose getPose();

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void setPose(EntityPose pose){
        if(!pose.equals(EntityPose.CROUCHING) || !this.getPose().equals(EntityPose.SWIMMING)) {
            this.dataTracker.set(POSE, pose);
        }
    }
}
