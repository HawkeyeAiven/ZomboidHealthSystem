package aiven.zomboidhealthsystem.network;

import dev.architectury.injectables.annotations.ExpectPlatform;

public final class ClientPacketFactory {

    private ClientPacketFactory() {

    }

    @ExpectPlatform
    public static Object createActionPacket(ActionType type, String bodyPartId, int index) {
        throw new AssertionError();
    }
}
