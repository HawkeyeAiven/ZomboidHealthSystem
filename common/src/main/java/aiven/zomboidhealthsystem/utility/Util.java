package aiven.zomboidhealthsystem.utility;

import dev.architectury.registry.registries.RegistrySupplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Random;
import java.util.function.Predicate;

public final class Util {

    private Util() {

    }

    public static ResourceLocation getId(Item item) {
        return BuiltInRegistries.ITEM.getKey(item);
    }

    public static ResourceLocation getId(RegistrySupplier<?> registryObject) {
        return registryObject.getId();
    }

    public static String getStringId(Item item) {
        return getId(item).toString();
    }

    public static String getStringId(RegistrySupplier<?> registryObject) {
        return getId(registryObject).toString();
    }

    public static boolean contains(Container container, Item item) {
        for(int i = 0; i < container.getContainerSize(); i++) {
            if(item.equals(container.getItem(i).getItem())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isInOpenSpace(Level level, BlockPos pos) {
        int count = 0;
        for (int i = 1; i < 10; i++) {
            Block block = level.getBlockState(pos.north(i)).getBlock();
            if (!isAir(block)) {
                count++;
                break;
            }
        }
        for (int i = 1; i < 10; i++) {
            Block block = level.getBlockState(pos.east(i)).getBlock();
            if (!isAir(block)) {
                count++;
                break;
            }
        }
        for (int i = -1; i > -10; i--) {
            Block block = level.getBlockState(pos.north(i)).getBlock();
            if (!isAir(block)) {
                count++;
                break;
            }
        }
        for (int i = -1; i > -10; i--) {
            Block block = level.getBlockState(pos.east(i)).getBlock();
            if (!isAir(block)) {
                count++;
                break;
            }
        }
        return count <= 2;
    }

    private static boolean isAir(Block block) {
        return block.equals(Blocks.AIR);
    }

    public static float getMinDistance(Level level, BlockPos center, int radius, Predicate<BlockState> filter) {
        float distance = -1;
        for(int x = -radius; x <= radius; x++){
            for(int y = -radius; y <= radius; y++){
                for(int z = -radius; z <= radius; z++){
                    BlockPos pos = new BlockPos(x + center.getX(),y + center.getY(),z + center.getZ());
                    BlockState blockState = level.getBlockState(pos);
                    if(filter.test(blockState)) {
                        float dis = (float) center.getCenter().distanceTo(pos.getCenter());
                        if (dis <= radius) {
                            if (distance != -1) {
                                distance = Math.min(dis, distance);
                            } else {
                                distance = dis;
                            }
                        }
                    }
                }
            }
        }
        return distance;
    }

    public static int getArmorCount(Player player) {
        int d = 0;

        for(ItemStack itemStack : player.getArmorSlots()) {
            if(!itemStack.isEmpty()) {
                d += 1;
            }
        }

        return d;
    }

    public static void addMobEffect(LivingEntity entity, MobEffect effect, int amplifier, int duration, boolean particles, boolean icon) {
        entity.addEffect(new MobEffectInstance(effect, amplifier, duration, false, particles, icon));
    }

    public static DamageSource getDamageSource(ResourceKey<DamageType> damageTypeResourceKey, Level level) {
        return new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(damageTypeResourceKey));
    }

    public static boolean random(float chance) {
        chance = clamp(chance, 0.0F, 1.0F);
        if(chance == 1.0F) {
            return true;
        } else if(chance == 0.0F) {
            return false;
        }

        Random random = new Random();
        return chance > random.nextFloat(0.0F, 1.0F);
    }

    public static float clamp(float value, float min, float max) {
        value = Math.min(value, max);
        value = Math.max(value, min);
        return value;
    }

    public static double floor(double number, int numbersAfterComma){
        double d = Math.pow(10, numbersAfterComma);
        int num = (int) (number * d);
        return (double) num / d;
    }
}
