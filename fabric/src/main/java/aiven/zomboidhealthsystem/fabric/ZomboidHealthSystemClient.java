package aiven.zomboidhealthsystem.fabric;

import net.fabricmc.api.ClientModInitializer;

public final class ZomboidHealthSystemClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        aiven.zomboidhealthsystem.ZomboidHealthSystemClient.initialize();
        ModClientEventCallbacks.initialize();
    }
}
