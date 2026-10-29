package aiven.zomboidhealthsystem.network;

import aiven.zomboidhealthsystem.ZomboidHealthSystem;
import aiven.zomboidhealthsystem.network.packet.ActionPacket;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;

import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Function;

public final class ClientPacketTypes {

    private ClientPacketTypes() {

    }

    public static final PacketType<ActionPacket> actionPacketType = create("action", ActionPacket::create);

    private static <T extends FabricPacket> PacketType<T> create(String id, Function<FriendlyByteBuf, T> constructor) {
        return PacketType.create(ZomboidHealthSystem.getResourceLocation(id), constructor);
    }
}
