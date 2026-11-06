package aiven.zomboidhealthsystem.fabric;

import net.fabricmc.api.ModInitializer;

public final class ZomboidHealthSystem implements ModInitializer {
    @Override
    public void onInitialize() {
        aiven.zomboidhealthsystem.ZomboidHealthSystem.initialize();
    }
}
