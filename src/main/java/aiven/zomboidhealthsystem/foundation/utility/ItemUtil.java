package aiven.zomboidhealthsystem.foundation.utility;

import aiven.zomboidhealthsystem.ModItems;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.util.HashMap;

public class ItemUtil {
    private static final HashMap<Item, Float> THIRST_MAP = new HashMap<>();

    public static float getThirst(Item item) {
        Float amount = THIRST_MAP.get(item);
        return amount != null ? amount : -1;
    }

    public static void initialize() {
        THIRST_MAP.put(Items.APPLE, 0.2F);
        THIRST_MAP.put(Items.MUSHROOM_STEW, 0.4F);
        THIRST_MAP.put(Items.MELON_SLICE, 0.4F);
        THIRST_MAP.put(Items.BEETROOT_SOUP, 0.4F);
        THIRST_MAP.put(Items.SWEET_BERRIES, 0.05F);
        THIRST_MAP.put(Items.GLOW_BERRIES, 0.05F);
        THIRST_MAP.put(Items.RABBIT_STEW, 0.35F);
        THIRST_MAP.put(Items.MILK_BUCKET, 2.0F);
        THIRST_MAP.put(ModItems.BOTTLE_OF_PURIFIED_WATER, 1.25F);
        THIRST_MAP.put(ModItems.CUP_OF_COFFEE, 1.0F);
        THIRST_MAP.put(ModItems.BOTTLE_OF_WINE, 1.25F);
    }
}
