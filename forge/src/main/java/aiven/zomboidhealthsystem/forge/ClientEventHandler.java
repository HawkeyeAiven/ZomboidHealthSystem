package aiven.zomboidhealthsystem.forge;

import aiven.zomboidhealthsystem.ModKeyBindings;
import aiven.zomboidhealthsystem.ZomboidHealthSystem;
import aiven.zomboidhealthsystem.ZomboidHealthSystemClient;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ZomboidHealthSystem.ID, bus= Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class ClientEventHandler {

    @SubscribeEvent
    public static void renderGui(RenderGuiEvent event) {
        if(event instanceof RenderGuiEvent.Post) {
            ZomboidHealthSystemClient.getHud().render(event.getGuiGraphics(), event.getPartialTick());
        }
    }

    @SubscribeEvent
    public static void renderGuiOverlay(RenderGuiOverlayEvent event) {
        if(event.getOverlay().id().equals(VanillaGuiOverlay.PLAYER_HEALTH.id()) || event.getOverlay().id().equals(VanillaGuiOverlay.FOOD_LEVEL.id())) {
            event.setResult(Event.Result.DENY);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if(event.phase == TickEvent.Phase.END) {
            for(KeyMapping keyMapping : ModKeyBindings.keyMappings.keySet()) {
                if(keyMapping.consumeClick()) {
                    ModKeyBindings.keyMappings.get(keyMapping).run();
                }
            }
        }
    }
}
