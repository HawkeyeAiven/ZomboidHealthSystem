package aiven.zomboidhealthsystem.foundation.utility;

import aiven.zomboidhealthsystem.ZomboidHealthSystem;
import aiven.zomboidhealthsystem.foundation.items.BandageItem;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Util {
    public static boolean random(float chance) {
        return chance > ZomboidHealthSystem.RANDOM.nextFloat(0, 1.0F);
    }

    public static int getArmorCount(PlayerEntity player) {
        int d = 0;

        for(ItemStack itemStack : player.getArmorItems()) {
            if(!itemStack.getItem().equals(Items.AIR)) {
                d += 1;
            }
        }

        return d;
    }

    public static float getDistance(World world, Block block, BlockPos center, int radius) {
        float distance = -1;
        for(int x = -radius; x <= radius; x++){
            for(int y = -radius; y <= radius; y++){
                for(int z = -radius; z <= radius; z++){
                    BlockPos pos = new BlockPos(x + center.getX(),y + center.getY(),z + center.getZ());
                    if(world.getBlockState(pos).getBlock().equals(block)) {
                        float dis = (float) center.toCenterPos().distanceTo(pos.toCenterPos());
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

    public static DamageSource getDamageSource(RegistryKey<DamageType> type, World world){
        return new DamageSource(world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(type));
    }

    public static String reduce(String word, int bound) {
        StringBuilder string = new StringBuilder();

        for(char c : word.toCharArray()){
            if(MinecraftClient.getInstance().textRenderer.getWidth(string.toString()) < bound) {
                string.append(c);
            }

        }

        return string.toString();
    }

    public static boolean isInOpenSpace(World world, BlockPos pos) {
        if (world.getBlockState(pos).getBlock().equals(Blocks.CAVE_AIR)) {
            return false;
        }
        int count = 0;
        for (int i = 1; i < 10; i++) {
            Block block = world.getBlockState(pos.north(i)).getBlock();
            if (!isAir(block)) {
                count++;
                break;
            }
        }
        for (int i = 1; i < 10; i++) {
            Block block = world.getBlockState(pos.east(i)).getBlock();
            if (!isAir(block)) {
                count++;
                break;
            }
        }
        for (int i = -1; i > -10; i--) {
            Block block = world.getBlockState(pos.north(i)).getBlock();
            if (!isAir(block)) {
                count++;
                break;
            }
        }
        for (int i = -1; i > -10; i--) {
            Block block = world.getBlockState(pos.east(i)).getBlock();
            if (!isAir(block)) {
                count++;
                break;
            }
        }
        return count <= 2;
    }

    private static boolean isAir(Block block) {
        return block.getBlastResistance() == 0 && !block.equals(Blocks.WATER) && !block.equals(Blocks.LAVA) && !block.equals(Blocks.CAVE_AIR);
    }

    public static boolean inventoryContains(PlayerInventory inventory, Item item){
        for(int i = 0; i < inventory.size(); i++){
            if(inventory.getStack(i).getItem().equals(item)){
                return true;
            }
        }
        return false;
    }

    public static BandageItem getBandageItem(PlayerInventory inventory) {
        for(int i = 0; i < inventory.size(); i++) {
            if(inventory.getStack(i).getItem() instanceof BandageItem bandageItem) {
                return bandageItem;
            }
        }
        return null;
    }

    public static void addStatusEffect(PlayerEntity user, StatusEffect effect, int duration, int amplifier){
        if(amplifier < 0) {
            return;
        }
        StatusEffectInstance statusEffectInstance =
                new StatusEffectInstance(
                        effect,
                        duration,
                        amplifier,
                        false,
                        false,
                        true);
        user.addStatusEffect(statusEffectInstance);
    }

    public static double toDouble(float number){
        return Double.parseDouble(String.valueOf(number));
    }

    public static double floor(double number, int nums){
        int num = (int) (number * (double) nums);
        return (double) num / nums;
    }
}
