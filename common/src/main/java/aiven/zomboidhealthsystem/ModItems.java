package aiven.zomboidhealthsystem;

import aiven.zomboidhealthsystem.item.*;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ZomboidHealthSystem.ID, Registries.ITEM);

    private static final WoolArmorMaterial woolArmorMaterial = new WoolArmorMaterial();

    private ModItems() {

    }

    public static RegistrySupplier<Item> BANDAGE = register("bandage", () -> new Bandage());
    public static RegistrySupplier<Item> DIRTY_BANDAGE = register("dirty_bandage", () -> new DirtyAbstractBandage());
    public static RegistrySupplier<Item> RAG = register("rag", () -> new Rag());
    public static RegistrySupplier<Item> DIRTY_RAG = register("dirty_rag", () -> new DirtyRag());
    public static RegistrySupplier<Item> ALCOHOL_WIPE = register("alcohol_wipe", () -> new AlcoholWipe());
    public static RegistrySupplier<Item> PLASTER = register("plaster", () -> new Plaster());
    public static RegistrySupplier<Item> CUP_OF_COFFEE = register("cup_of_coffee", () -> new CupOfCoffee());
    public static RegistrySupplier<Item> BOTTLE_OF_PURIFIED_WATER = register("bottle_of_purified_water", () -> new BottleOfPurifiedWater());
    public static RegistrySupplier<Item> BOTTLE_OF_WINE = register("bottle_of_wine", () -> new BottleOfWine());
    public static RegistrySupplier<Item> EMPTY_CUP = register("empty_cup", () -> new EmptyCupItem());
    public static RegistrySupplier<Item> PAINKILLERS = register("painkillers", () -> new Painkillers());
    public static RegistrySupplier<Item> SLEEPING_PILLS = register("sleeping_pills", () -> new SleepingPills());
    public static RegistrySupplier<Item> THERMOMETER = register("thermometer", () -> new Thermometer());
    public static RegistrySupplier<Item> WOOL_HELMET = register("wool_helmet", () -> new ArmorItem(woolArmorMaterial, ArmorItem.Type.HELMET, new Item.Properties()));
    public static RegistrySupplier<Item> WOOL_CHESTPLATE = register("wool_chestplate", () -> new ArmorItem(woolArmorMaterial, ArmorItem.Type.CHESTPLATE, new Item.Properties()));
    public static RegistrySupplier<Item> WOOL_LEGGINGS = register("wool_leggings", () -> new ArmorItem(woolArmorMaterial, ArmorItem.Type.LEGGINGS, new Item.Properties()));
    public static RegistrySupplier<Item> WOOL_BOOTS = register("wool_boots", () -> new ArmorItem(woolArmorMaterial, ArmorItem.Type.BOOTS, new Item.Properties()));

    public static RegistrySupplier<Item> register(String id, Supplier<Item> item) {
        return ITEMS.register(id, item);
    }

    public static void initialize() {
        ITEMS.register();
    }
}
