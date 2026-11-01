package aiven.zomboidhealthsystem.utility;

import aiven.zomboidhealthsystem.ModItems;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.HashMap;

public final class ItemUtil {
    private static final HashMap<Item, Float> map = new HashMap<>();

    private ItemUtil() {

    }

    static {
        add(Items.APPLE, 0.2F);
        add(Items.MUSHROOM_STEW, 0.4F);
        add(Items.MELON_SLICE, 0.4F);
        add(Items.BEETROOT_SOUP, 0.4F);
        add(Items.SWEET_BERRIES, 0.05F);
        add(Items.GLOW_BERRIES, 0.05F);
        add(Items.RABBIT_STEW, 0.35F);
        add(Items.MILK_BUCKET, 2.0F);
        add(ModItems.BOTTLE_OF_PURIFIED_WATER, 1.25F);
        add(ModItems.CUP_OF_COFFEE, 0.75F);
        add(ModItems.BOTTLE_OF_WINE, 1.25F);
    }

    private static void add(Item item, float thirst) {
        map.put(item, thirst);
    }

    public static float getThirst(Item item) {
        Float amount = map.get(item);
        return amount != null ? amount : -1;
    }
}
