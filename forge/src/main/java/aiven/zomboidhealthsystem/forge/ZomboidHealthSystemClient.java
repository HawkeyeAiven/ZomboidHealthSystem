package aiven.zomboidhealthsystem.forge;

import aiven.zomboidhealthsystem.ZomboidHealthSystem;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = ZomboidHealthSystem.ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ZomboidHealthSystemClient {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        aiven.zomboidhealthsystem.ZomboidHealthSystemClient.initialize();
    }
}
