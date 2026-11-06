package aiven.zomboidhealthsystem.forge;

import dev.architectury.platform.forge.EventBuses;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(aiven.zomboidhealthsystem.ZomboidHealthSystem.ID)
public final class ZomboidHealthSystem {
    public ZomboidHealthSystem() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        EventBuses.registerModEventBus(aiven.zomboidhealthsystem.ZomboidHealthSystem.ID, eventBus);
        aiven.zomboidhealthsystem.ZomboidHealthSystem.initialize();
    }
}
