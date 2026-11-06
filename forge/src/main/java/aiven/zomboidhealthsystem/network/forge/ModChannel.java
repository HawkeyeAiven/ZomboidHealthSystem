package aiven.zomboidhealthsystem.network.forge;

import aiven.zomboidhealthsystem.ZomboidHealthSystem;

import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

final class ModChannel {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            ZomboidHealthSystem.getResourceLocation("main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static SimpleChannel getInstance() {
        return INSTANCE;
    }
}
