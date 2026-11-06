package aiven.zomboidhealthsystem.network;

import aiven.zomboidhealthsystem.health.Health;
import dev.architectury.injectables.annotations.ExpectPlatform;

public final class ServerPacketFactory {

    private ServerPacketFactory() {

    }

    @ExpectPlatform
    public static Object createHealthPacket(Health health) {
        throw new AssertionError();
    }
}
