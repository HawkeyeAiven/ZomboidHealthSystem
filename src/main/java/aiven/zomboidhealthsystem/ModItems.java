package aiven.zomboidhealthsystem;

import aiven.zomboidhealthsystem.item.*;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;

public final class ModItems {
    private static final WoolArmorMaterial woolArmorMaterial = new WoolArmorMaterial();

    private ModItems() {

    }

    public static Item BANDAGE = register("bandage", new Bandage());
    public static Item DIRTY_BANDAGE = register("dirty_bandage", new DirtyAbstractBandage());
    public static Item RAG = register("rag", new Rag());
    public static Item DIRTY_RAG = register("dirty_rag", new DirtyRag());
    public static Item ALCOHOL_WIPE = register("alcohol_wipe", new AlcoholWipe());
    public static Item PLASTER = register("plaster", new Plaster());
    public static Item CUP_OF_COFFEE = register("cup_of_coffee", new CupOfCoffee());
    public static Item BOTTLE_OF_PURIFIED_WATER = register("bottle_of_purified_water", new BottleOfPurifiedWater());
    public static Item BOTTLE_OF_WINE = register("bottle_of_wine", new BottleOfWine());
    public static Item EMPTY_CUP = register("empty_cup", new EmptyCupItem());
    public static Item PAINKILLERS = register("painkillers", new Painkillers());
    public static Item SLEEPING_PILLS = register("sleeping_pills", new SleepingPills());
    public static Item THERMOMETER = register("thermometer", new Thermometer());
    public static Item WOOL_HELMET = register("wool_helmet", new ArmorItem(woolArmorMaterial, ArmorItem.Type.HELMET, new Item.Properties()));
    public static Item WOOL_CHESTPLATE = register("wool_chestplate", new ArmorItem(woolArmorMaterial, ArmorItem.Type.CHESTPLATE, new Item.Properties()));
    public static Item WOOL_LEGGINGS = register("wool_leggings", new ArmorItem(woolArmorMaterial, ArmorItem.Type.LEGGINGS, new Item.Properties()));
    public static Item WOOL_BOOTS = register("wool_boots", new ArmorItem(woolArmorMaterial, ArmorItem.Type.BOOTS, new Item.Properties()));

    public static Item register(String id, Item item) {
        ResourceLocation itemID = ZomboidHealthSystem.getResourceLocation(id);
        return Registry.register(BuiltInRegistries.ITEM, itemID, item);
    }

    public static void initialize() {

    }
}
