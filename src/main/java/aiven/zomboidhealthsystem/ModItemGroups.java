package aiven.zomboidhealthsystem;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

public final class ModItemGroups {

    private ModItemGroups() {

    }

    public static CreativeModeTab MAIN_GROUP = register(ModItems.BANDAGE,ZomboidHealthSystem.NAME, ZomboidHealthSystem.ID,
            ModItems.BANDAGE,
            ModItems.RAG,
            ModItems.ALCOHOL_WIPE,
            ModItems.PLASTER,
            ModItems.PAINKILLERS,
            ModItems.SLEEPING_PILLS,
            ModItems.CUP_OF_COFFEE,
            ModItems.EMPTY_CUP,
            ModItems.BOTTLE_OF_PURIFIED_WATER,
            ModItems.BOTTLE_OF_WINE,
            ModItems.THERMOMETER,
            ModItems.WOOL_HELMET,
            ModItems.WOOL_CHESTPLATE,
            ModItems.WOOL_LEGGINGS,
            ModItems.WOOL_BOOTS
    );

    public static CreativeModeTab register(Item icon, String name, String id, Item...items) {
        ResourceKey<CreativeModeTab> key = ResourceKey.create(BuiltInRegistries.CREATIVE_MODE_TAB.key(), new ResourceLocation(ZomboidHealthSystem.ID, id));
        CreativeModeTab creativeModeTab = CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
                .icon(icon::getDefaultInstance)
                .title(Component.literal(name))
                .build();

        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, key, creativeModeTab);

        ItemGroupEvents.modifyEntriesEvent(key).register(itemGroup -> {
            for(Item item : items) {
                itemGroup.accept(item);
            }
        });

        return creativeModeTab;
    }

    public static void initialize() {

    }
}
