package aiven.zomboidhealthsystem.network;

import dev.architectury.injectables.annotations.ExpectPlatform;

public final class ClientNetworking {

    private ClientNetworking() {

    }

    @ExpectPlatform
    public static void send(Object packet) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void initialize() {
        throw new AssertionError();
    }
}
