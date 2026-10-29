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

    public static Item BANDAGE = register(new Bandage(),"bandage");
    public static Item DIRTY_BANDAGE = register(new DirtyAbstractBandage(),"dirty_bandage");

    public static Item RAG = register(new Rag(),"rag");
    public static Item DIRTY_RAG = register(new DirtyRag(),"dirty_rag");

    public static Item ALCOHOL_WIPE = register(new AlcoholWipe(),"alcohol_wipe");

    public static Item PLASTER = register(new Plaster(),"plaster");

    public static Item CUP_OF_COFFEE = register(new CupOfCoffee(),"cup_of_coffee");
    public static Item BOTTLE_OF_PURIFIED_WATER = register(new BottleOfPurifiedWater(),"bottle_of_purified_water");
    public static Item BOTTLE_OF_WINE = register(new BottleOfWine(), "bottle_of_wine");

    public static Item EMPTY_CUP = register(new EmptyCupItem(),"empty_cup");

    public static Item PAINKILLERS = register(new Painkillers(),"painkillers");

    public static Item SLEEPING_PILLS = register(new SleepingPills(), "sleeping_pills");

    public static Item THERMOMETER = register(new Thermometer(),"thermometer");

    public static Item WOOL_HELMET = register(
            new ArmorItem(
                    woolArmorMaterial,
                    ArmorItem.Type.HELMET,
                    new Item.Properties()
            ),
            "wool_helmet"
    );

    public static Item WOOL_CHESTPLATE = register(
            new ArmorItem(
                    woolArmorMaterial,
                    ArmorItem.Type.CHESTPLATE,
                    new Item.Properties()
            ),
            "wool_chestplate"
    );

    public static Item WOOL_LEGGINGS = register(
            new ArmorItem(
                    woolArmorMaterial,
                    ArmorItem.Type.LEGGINGS,
                    new Item.Properties()
            ),
            "wool_leggings"
    );

    public static Item WOOL_BOOTS = register(
            new ArmorItem(
                    woolArmorMaterial,
                    ArmorItem.Type.BOOTS,
                    new Item.Properties()
            ),
            "wool_boots"
    );

    public static Item register(Item item, String id) {
        ResourceLocation itemID = ZomboidHealthSystem.getResourceLocation(id);
        return Registry.register(BuiltInRegistries.ITEM, itemID, item);
    }

    public static void initialize() {

    }
}
