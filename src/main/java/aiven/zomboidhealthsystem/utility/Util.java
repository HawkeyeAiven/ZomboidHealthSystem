package aiven.zomboidhealthsystem.utility;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.Container;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.Random;

public final class Util {

    private Util() {

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
        if (level.getBlockState(pos).getBlock().equals(Blocks.CAVE_AIR)) {
            return false;
        }
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
        return block.defaultBlockState().isAir();
    }

    public static float getDistance(Level level, Block block, BlockPos center, int radius) {
        float distance = -1;
        for(int x = -radius; x <= radius; x++){
            for(int y = -radius; y <= radius; y++){
                for(int z = -radius; z <= radius; z++){
                    BlockPos pos = new BlockPos(x + center.getX(),y + center.getY(),z + center.getZ());
                    if(level.getBlockState(pos).getBlock().equals(block)) {
                        float dis = (float) center.getCenter().distanceTo(pos.getCenter());
                        if(dis <= radius) {
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
            if(!itemStack.getItem().equals(Items.AIR)) {
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

    public static double floor(double number, int nums){
        int num = (int) (number * (double) nums);
        return (double) num / nums;
    }

    public static float floor(float number, int nums){
        int num = (int) (number * (float) nums);
        return (float) num / nums;
    }
}
