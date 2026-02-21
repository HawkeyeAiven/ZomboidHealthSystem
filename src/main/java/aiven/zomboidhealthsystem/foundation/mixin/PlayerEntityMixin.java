package aiven.zomboidhealthsystem.foundation.mixin;

import aiven.zomboidhealthsystem.Config;
import aiven.zomboidhealthsystem.foundation.player.Health;
import aiven.zomboidhealthsystem.foundation.player.bodyparts.BodyPart;
import aiven.zomboidhealthsystem.foundation.player.moodles.Exhaustion;
import aiven.zomboidhealthsystem.foundation.player.moodles.Thirst;
import aiven.zomboidhealthsystem.foundation.world.ModServer;
import com.mojang.datafixers.util.Either;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;

@Mixin(value = PlayerEntity.class, priority = 10000)
public abstract class PlayerEntityMixin extends LivingEntityMixin {

    @Shadow public abstract boolean isInvulnerableTo(DamageSource damageSource);

    @Shadow @Final private PlayerAbilities abilities;

    @Shadow protected abstract void dropShoulderEntities();

    @Shadow public abstract void onDeath(DamageSource damageSource);

    @Shadow public abstract Text getName();

    @Shadow public abstract void wakeUp();

    @Shadow protected abstract void damageShield(float amount);

    @Shadow protected abstract void takeShieldHit(LivingEntity attacker);

    @Shadow public abstract ItemStack getEquippedStack(EquipmentSlot slot);

    @Shadow protected abstract void damageHelmet(DamageSource source, float amount);

    @Shadow public abstract void incrementStat(Identifier stat);

    @Shadow public abstract void addExhaustion(float exhaustion);

    @Shadow public abstract boolean isSwimming();

    @Shadow public abstract boolean isSpectator();

    @Shadow public abstract boolean isCreative();

    @Shadow public int experiencePickUpDelay;
    @Shadow private int sleepTimer;

    @Shadow protected abstract boolean updateWaterSubmersionState();

    @Shadow public ScreenHandler currentScreenHandler;

    @Shadow protected abstract void closeHandledScreen();

    @Shadow @Final public PlayerScreenHandler playerScreenHandler;

    @Shadow protected abstract void updateCapeAngles();

    @Shadow protected HungerManager hungerManager;
    @Shadow private ItemStack selectedItem;

    @Shadow public abstract void resetLastAttackedTicks();

    @Shadow protected abstract void updateTurtleHelmet();

    @Shadow @Final private ItemCooldownManager itemCooldownManager;

    @Shadow public abstract void sendMessage(Text message, boolean overlay);

    @Unique
    public Health modHealth;

    /**
     * @author Я
     * @reason есть
     */
    @Overwrite
    public boolean damage(DamageSource source,float amount) throws IOException {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else if (this.abilities.invulnerable && !source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return false;
        } else {
            this.despawnCounter = 0;
            if (this.isDead()) {
                return false;
            } else {
                if (!this.getWorld().isClient) {
                    this.dropShoulderEntities();
                }

                if (source.isScaledWithDifficulty()) {
                    if (this.getWorld().getDifficulty() == Difficulty.PEACEFUL) {
                        amount = 0.0F;
                    }

                    if (this.getWorld().getDifficulty() == Difficulty.EASY) {
                        amount = Math.min(amount / 2.0F + 1.0F, amount);
                    }

                    if (this.getWorld().getDifficulty() == Difficulty.HARD) {
                        amount = amount * 3.0F / 2.0F;
                    }
                }

                if (source.isIn(DamageTypeTags.IS_FIRE) && this.hasStatusEffect(StatusEffects.FIRE_RESISTANCE)) {
                    return false;
                } else {
                    if (this.isSleeping() && !this.getWorld().isClient) {
                        this.wakeUp();
                    }

                    this.despawnCounter = 0;
                    float f = amount;
                    boolean bl = false;
                    float g = 0.0F;
                    if (amount > 0.0F && this.blockedByShield(source)) {
                        this.damageShield(amount);
                        g = amount;
                        amount = 0.0F;
                        if (!source.isIn(DamageTypeTags.IS_PROJECTILE)) {
                            Entity entity = source.getSource();
                            if (entity instanceof LivingEntity livingEntity) {
                                this.takeShieldHit(livingEntity);
                            }
                        }

                        bl = true;
                    }

                    if (source.isIn(DamageTypeTags.IS_FREEZING) && this.getType().isIn(EntityTypeTags.FREEZE_HURTS_EXTRA_TYPES)) {
                        amount *= 5.0F;
                    }

                    this.limbAnimator.setSpeed(1.5F);
                    boolean bl2 = true;
                    if ((float)this.timeUntilRegen > 10.0F && !source.isIn(DamageTypeTags.BYPASSES_COOLDOWN)) {
                        if (amount <= this.lastDamageTaken) {
                            return false;
                        }

                        this.lastDamageTaken = amount;
                        bl2 = false;
                    } else {
                        this.lastDamageTaken = amount;
                        this.timeUntilRegen = 20;
                        this.maxHurtTime = 10;
                        this.hurtTime = this.maxHurtTime;
                    }

                    if (source.isIn(DamageTypeTags.DAMAGES_HELMET) && !this.getEquippedStack(EquipmentSlot.HEAD).isEmpty()) {
                        this.damageHelmet(source, amount);
                        amount *= 0.75F;
                    }

                    Entity entity2 = source.getAttacker();
                    if (entity2 != null) {
                        if (entity2 instanceof LivingEntity livingEntity2) {
                            if (!source.isIn(DamageTypeTags.NO_ANGER)) {
                                this.setAttacker(livingEntity2);
                            }
                        }

                        if (entity2 instanceof PlayerEntity playerEntity) {
                            this.playerHitTimer = 100;
                            this.attackingPlayer = playerEntity;
                        } else if (entity2 instanceof WolfEntity wolfEntity) {
                            if (wolfEntity.isTamed()) {
                                this.playerHitTimer = 100;
                                LivingEntity var11 = wolfEntity.getOwner();
                                if (var11 instanceof PlayerEntity) {
                                    this.attackingPlayer = (PlayerEntity)var11;
                                } else {
                                    this.attackingPlayer = null;
                                }
                            }
                        }
                    }

                    if (bl2) {
                        if (bl) {
                            this.getWorld().sendEntityStatus(this.toPlayerEntity(), (byte)29);
                        } else {
                            this.getWorld().sendEntityDamage(this.toPlayerEntity(), source);
                        }

                        if (!source.isIn(DamageTypeTags.NO_IMPACT) && (!bl || amount > 0.0F)) {
                            this.scheduleVelocityUpdate();
                        }

                        if (entity2 != null && !source.isIn(DamageTypeTags.IS_EXPLOSION)) {
                            double d = entity2.getX() - this.getX();

                            double e;
                            for(e = entity2.getZ() - this.getZ(); d * d + e * e < 1.0E-4; e = (Math.random() - Math.random()) * 0.01) {
                                d = (Math.random() - Math.random()) * 0.01;
                            }

                            this.takeKnockback(0.4F, d, e);
                            if (!bl) {
                                this.tiltScreen(d, e);
                            }
                        }
                    }

                    boolean bl3 = !bl || amount > 0.0F;

                    if (toPlayerEntity() instanceof ServerPlayerEntity) {
                        Criteria.ENTITY_HURT_PLAYER.trigger((ServerPlayerEntity) toPlayerEntity(), source, f, amount, bl);
                        if (g > 0.0F && g < 3.4028235E37F) {
                            (toPlayerEntity()).increaseStat(Stats.DAMAGE_BLOCKED_BY_SHIELD, Math.round(g * 10.0F));
                        }
                    }

                    if (entity2 instanceof ServerPlayerEntity) {
                        Criteria.PLAYER_HURT_ENTITY.trigger((ServerPlayerEntity)entity2, toPlayerEntity(), source, f, amount, bl);
                    }
                    amount = this.applyArmorToDamage(source,amount);
                    amount = this.modifyAppliedDamage(source,amount);

                    if(bl3 && !this.getWorld().isClient){
                        damageHealth(source,amount);
                    }

                    return bl3;
                }
            }
        }
    }

    @Inject(at = @At("TAIL"), method = "trySleep")
    private void trySleep(BlockPos pos, CallbackInfoReturnable<Either<PlayerEntity.SleepFailureReason, Unit>> cir) {
        this.sleepTimer = 0;
        if(!getWorld().isClient()) {
            if(getModHealth().getDrowsiness().getAmplifier() < Config.MIN_DROWSINESS_FOR_SLEEP.getValue()) {
                this.toPlayerEntity().wakeUp();
                this.sendMessage(Text.translatable("zomboidhealthsystem.message.dont_want_sleep"), true);
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "readCustomDataFromNbt")
    private void readNbt(NbtCompound nbt, CallbackInfo ci) {
        if(!this.getWorld().isClient) {
            String string = nbt.getString("mod_health");
            if(string == null) {
                this.modHealth = new Health(toPlayerEntity());
            } else {
                this.modHealth = Health.parseHealth(toPlayerEntity(), string);
            }
            ModServer.registerPlayer(toPlayerEntity(), getModHealth());
        }
    }

    @Inject(at = @At("HEAD"), method = "writeCustomDataToNbt")
    private void writeNbt(NbtCompound nbt, CallbackInfo ci) {
        if(!this.getWorld().isClient) {
            nbt.putString("mod_health", getModHealth().toString());
        }
    }

    @Inject(at = @At("HEAD"), method = "dropInventory")
    private void dropInventory(CallbackInfo ci) {
        if(!getWorld().isClient()) {
            for(BodyPart part : getModHealth().getBodyParts()) {
                if(part.isBandaged()) {
                    toPlayerEntity().dropItem(part.unBandage().getDefaultStack(), true, true);
                }
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "eatFood")
    private void eatFood(World world, ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
        if(!getWorld().isClient()) {
            Thirst thirst = getModHealth().getThirst();
            Item item = stack.getItem();
            if (item.equals(Items.APPLE)) {
                thirst.drink(0.2F, true);
            } else if(item.equals(Items.MUSHROOM_STEW)) {
                thirst.drink(0.4F, true);
            } else if(item.equals(Items.MELON_SLICE)) {
                thirst.drink(0.4F, true);
            }
        }
    }

    /**
     * @author Aiven
     * @reason No
     */
    @Overwrite
    public boolean canFoodHeal(){
        return false;
    }

    /**
     * @author Aiven
     * @reason No
     */
    @Overwrite
    public void jump() {
        if(!this.isCrawling()) {
            Vec3d vec3d = this.getVelocity();
            this.setVelocity(vec3d.x, this.getJumpVelocity(), vec3d.z);
            if (this.isSprinting()) {
                float f = this.getYaw() * ((float) Math.PI / 180F);
                this.setVelocity(this.getVelocity().add((-MathHelper.sin(f) * 0.2F), 0.0F, (MathHelper.cos(f) * 0.2F)));
            }
            this.velocityDirty = true;

            this.incrementStat(Stats.JUMP);
            if (this.isSprinting()) {
                this.addExhaustion(0.2F);
            } else {
                this.addExhaustion(0.05F);
            }
            if(!this.getWorld().isClient() && !this.isCreative() && !this.isSpectator()) {
                Exhaustion exhaustion = this.getModHealth().getExhaustion();
                exhaustion.onJump();
            }
        }
    }

    @Unique
    boolean isSwimming = false;
    /**
     * @author Aiven
     * @reason No
     */
    @Overwrite
    public void updatePose(){
        if (this.wouldPoseNotCollide(EntityPose.SWIMMING)) {
            EntityPose entityPose;
            if (this.isFallFlying()) {
                entityPose = EntityPose.FALL_FLYING;
            } else if (this.isSleeping()) {
                entityPose = EntityPose.SLEEPING;
            } else if (this.isSwimming()) {
                isSwimming = true;
                entityPose = EntityPose.SWIMMING;
            } else if (this.isUsingRiptide()) {
                entityPose = EntityPose.SPIN_ATTACK;
            } else if (this.isSneaking() && !this.abilities.flying) {
                entityPose = EntityPose.CROUCHING;
            } else if (this.isCrawling()) {
                if(isSwimming){
                    entityPose = EntityPose.STANDING;
                    isSwimming = false;
                } else {
                    entityPose = EntityPose.SWIMMING;
                }
            } else {
                entityPose = EntityPose.STANDING;
            }

            EntityPose entityPose2;
            if (!this.isSpectator() && !this.hasVehicle() && !this.wouldPoseNotCollide(entityPose)) {
                if (this.wouldPoseNotCollide(EntityPose.CROUCHING)) {
                    entityPose2 = EntityPose.CROUCHING;
                } else {
                    entityPose2 = EntityPose.SWIMMING;
                }
            } else {
                entityPose2 = entityPose;
            }

            this.setPose(entityPose2);
        }
    }

    /**
     * @author Aiven
     * @reason No
     */
    @Overwrite
    public boolean canConsume(boolean ignoreHunger) {
        if(!this.getWorld().isClient()) {
            return this.abilities.invulnerable || ignoreHunger || this.getModHealth().getHunger().canEat();
        } else {
            return false;
        }
    }

    @Override
    public void heal(float amount) {
        if(!this.getWorld().isClient) {
            getModHealth().healRandom(amount / 3);
            getModHealth().healPlayerHp(amount * 2);
            sendPacketHealth();
        }
    }

    @Unique
    public PlayerEntity toPlayerEntity() {
        return (PlayerEntity) (Object) (this);
    }

    @Unique
    public void damageHealth(DamageSource source, float amount){
        if(!getWorld().isClient) {
            getModHealth().damage(source, amount * Config.DAMAGE_MULTIPLIER.getValue());
            sendPacketHealth();
            sendPacketDamage();
        }
    }

    @Unique
    public Health getModHealth(){
        return modHealth;
    }

    @Unique
    public void sendPacketHealth() {
        if(!getWorld().isClient) {
            ModServer.sendPacketHealth((ServerPlayerEntity) toPlayerEntity());
        }
    }

    @Unique
    public void sendPacketDamage(){
        if(!getWorld().isClient) {
            ModServer.sendPacketDamage((ServerPlayerEntity) toPlayerEntity());
        }
    }
}