package aiven.zomboidhealthsystem;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {
    public static ItemGroup MAIN_GROUP = register(ModItems.BANDAGE,ZomboidHealthSystem.NAME, ZomboidHealthSystem.ID,
            ModItems.BANDAGE,
            ModItems.RAG,
            ModItems.ALCOHOL_WIPE,
            ModItems.PLASTER,
            ModItems.PAINKILLERS,
            ModItems.SLEEPING_PILLS,
            ModItems.COFFEE,
            ModItems.EMPTY_CUP,
            ModItems.PURIFIED_WATER_BOTTLE,
            ModItems.THERMOMETER,
            ModItems.WOOL_HELMET,
            ModItems.WOOL_CHESTPLATE,
            ModItems.WOOL_LEGGINGS,
            ModItems.WOOL_BOOTS
    );

    private static ItemGroup register(Item icon, String name, String id, Item...items){
        final RegistryKey<ItemGroup> groupKey = RegistryKey.of(Registries.ITEM_GROUP.getKey(), new Identifier(ZomboidHealthSystem.ID, id));
        final ItemGroup group = FabricItemGroup.builder()
                .icon(() -> new ItemStack(icon))
                .displayName(Text.translatable(name))
                .build();

        ItemGroup itemGroup = Registry.register(Registries.ITEM_GROUP, groupKey, group);
        for(Item i:items) {
            ItemGroupEvents.modifyEntriesEvent(groupKey).register(itemGroup1 -> {
                itemGroup1.add(i);
            });
        }
        return itemGroup;
    }

    public static void initialize(){}
}
