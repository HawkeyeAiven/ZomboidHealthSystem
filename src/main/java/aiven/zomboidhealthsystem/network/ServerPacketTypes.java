package aiven.zomboidhealthsystem.network;

import aiven.zomboidhealthsystem.ZomboidHealthSystem;
import aiven.zomboidhealthsystem.network.packet.ModHealthPacket;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Function;

public final class ServerPacketTypes {

    private ServerPacketTypes() {

    }

    public static final PacketType<ModHealthPacket> healthPacketType = create("health", ModHealthPacket::create);

    private static <T extends FabricPacket> PacketType<T> create(String id, Function<FriendlyByteBuf, T> constructor) {
        return PacketType.create(ZomboidHealthSystem.getResourceLocation(id), constructor);
    }
}
