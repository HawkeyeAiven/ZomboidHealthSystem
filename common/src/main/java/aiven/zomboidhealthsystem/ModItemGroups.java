package aiven.zomboidhealthsystem;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

public final class ModItemGroups {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(ZomboidHealthSystem.ID, Registries.CREATIVE_MODE_TAB);

    private ModItemGroups() {

    }

    public static final RegistrySupplier<CreativeModeTab> MAIN_GROUP = register(ModItems.BANDAGE, ZomboidHealthSystem.NAME, ZomboidHealthSystem.ID,
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

    @SafeVarargs
    private static RegistrySupplier<CreativeModeTab> register(RegistrySupplier<Item> icon, String name, String id, RegistrySupplier<Item>...items) {
        return TABS.register(id, () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
                .icon(() -> icon.get().getDefaultInstance())
                .title(Component.literal(name))
                .displayItems((parameters, out) -> {
                    for(RegistrySupplier<Item> item : items) {
                        out.accept(item.get());
                    }
                })
                .build()
        );
    }

    public static void initialize() {
        TABS.register();
    }
}
