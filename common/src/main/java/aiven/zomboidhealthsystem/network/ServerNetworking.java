package aiven.zomboidhealthsystem.network;

import dev.architectury.injectables.annotations.ExpectPlatform;

import net.minecraft.server.level.ServerPlayer;

public final class ServerNetworking {

    private ServerNetworking() {

    }

    @ExpectPlatform
    public static void send(ServerPlayer player, Object packet) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void initialize() {
        throw new AssertionError();
    }
}
