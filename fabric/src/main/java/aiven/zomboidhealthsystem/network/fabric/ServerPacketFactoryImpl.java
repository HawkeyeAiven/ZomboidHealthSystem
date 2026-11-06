package aiven.zomboidhealthsystem.network.fabric;

import aiven.zomboidhealthsystem.network.fabric.packet.HealthPacket;
import aiven.zomboidhealthsystem.health.Health;

public final class ServerPacketFactoryImpl {
    public static Object createHealthPacket(Health health) {
        return new HealthPacket(health);
    }
}
