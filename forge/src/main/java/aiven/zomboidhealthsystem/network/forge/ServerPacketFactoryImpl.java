package aiven.zomboidhealthsystem.network.forge;

import aiven.zomboidhealthsystem.network.forge.packet.HealthPacket;
import aiven.zomboidhealthsystem.health.Health;

public final class ServerPacketFactoryImpl {

    private ServerPacketFactoryImpl() {

    }

    public static Object createHealthPacket(Health health) {
        return new HealthPacket(health);
    }
}
