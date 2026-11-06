package aiven.zomboidhealthsystem.network.forge;

import aiven.zomboidhealthsystem.network.forge.packet.ActionPacket;
import aiven.zomboidhealthsystem.network.ActionType;

public final class ClientPacketFactoryImpl {

    private ClientPacketFactoryImpl() {

    }

    public static Object createActionPacket(ActionType type, String bodyPartId, int index) {
        return new ActionPacket(type, bodyPartId, index);
    }
}
