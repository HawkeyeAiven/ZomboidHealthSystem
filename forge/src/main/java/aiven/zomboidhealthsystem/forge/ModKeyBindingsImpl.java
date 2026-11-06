package aiven.zomboidhealthsystem.forge;

import aiven.zomboidhealthsystem.ModKeyBindings;
import aiven.zomboidhealthsystem.ZomboidHealthSystem;

import net.minecraft.client.KeyMapping;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ZomboidHealthSystem.ID, bus= Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ModKeyBindingsImpl {

    private ModKeyBindingsImpl() {

    }

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        for(KeyMapping keyMapping : ModKeyBindings.keyMappings.keySet()) {
            event.register(keyMapping);
        }
    }

    public static void initialize() {
    }
}
