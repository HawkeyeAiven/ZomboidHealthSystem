package aiven.zomboidhealthsystem.mixin.mixin;

import aiven.zomboidhealthsystem.health.Health;
import aiven.zomboidhealthsystem.mixin.access.PlayerAccess;
import aiven.zomboidhealthsystem.health.moodles.Hunger;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.CombatTracker;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

import org.spongepowered.asm.mixin.*;

@Mixin(value = LivingEntity.class, priority = 10000)
public abstract class LivingEntityMixin extends EntityMixin {

    @Shadow
    public abstract SoundEvent getEatingSound(ItemStack itemStack);

    @Shadow
    protected abstract void addEatEffect(ItemStack itemStack, Level level, LivingEntity livingEntity);

    @Shadow
    public abstract void setHealth(float f);

    @Shadow
    protected abstract float getDamageAfterArmorAbsorb(DamageSource damageSource, float f);

    @Shadow
    protected abstract float getDamageAfterMagicAbsorb(DamageSource damageSource, float f);

    @Shadow
    public abstract CombatTracker getCombatTracker();

    @Shadow
    @Final
    private static EntityDataAccessor<Float> DATA_HEALTH_ID;

    /**
     * @author Aiven
     * @reason Перенаправление на {@link Hunger#eatFood(int, float)} )}
     */
    @Overwrite
    public ItemStack eat(Level level, ItemStack itemStack) {
        if(toLivingEntity() instanceof PlayerAccess playerAccess) {
            if (itemStack.isEdible()) {
                FoodProperties foodProperties = itemStack.getItem().getFoodProperties();
                playerAccess.zomboid_health_system$getModHealth().getHunger().eatFood(foodProperties.getNutrition(), foodProperties.getSaturationModifier());
            }
        }
        if (itemStack.isEdible()) {
            level.playSound(
                    null,
                    this.getX(),
                    this.getY(),
                    this.getZ(),
                    this.getEatingSound(itemStack),
                    SoundSource.NEUTRAL,
                    1.0F,
                    1.0F + (level.random.nextFloat() - level.random.nextFloat()) * 0.4F
            );
            this.addEatEffect(itemStack, level, toLivingEntity());
            if (!(toLivingEntity() instanceof Player) || !((Player)toLivingEntity()).getAbilities().instabuild) {
                itemStack.shrink(1);
            }

            this.gameEvent(GameEvent.EAT);
        }

        return itemStack;
    }

    /**
     * @author Aiven
     * @reason К сожалению, вместо того, чтобы использовать метод isAlive(), майнкрафт часто обращается к этому методу.
     */
    @Overwrite
    public float getHealth() {
        if(toLivingEntity() instanceof PlayerAccess playerAccess) {
            Health health = playerAccess.zomboid_health_system$getModHealth();
            if(!health.isAlive()) {
                return 0.0F;
            }
        }
        return this.entityData.get(DATA_HEALTH_ID);
    }

    /**
     * @author Aiven
     * @reason Перенаправление на {@link Health#isAlive()}
     */
    @Overwrite
    public boolean isDeadOrDying() {
        if(toLivingEntity() instanceof PlayerAccess playerAccess) {
            return !playerAccess.zomboid_health_system$getModHealth().isAlive() || getHealth() == 0;
        } else {
            return this.getHealth() <= 0.0F;
        }
    }

    /**
     * @author Aiven
     * @reason Перенаправление на {@link Health#isAlive()}
     */
    @Overwrite
    public boolean isAlive() {
        if(toLivingEntity() instanceof PlayerAccess playerAccess) {
            return playerAccess.zomboid_health_system$getModHealth().isAlive() && getHealth() != 0;
        } else {
            return !this.isRemoved() && this.getHealth() > 0.0F;
        }
    }

    @Unique
    public final LivingEntity toLivingEntity() {
        return (LivingEntity) ((Object)this);
    }
}
