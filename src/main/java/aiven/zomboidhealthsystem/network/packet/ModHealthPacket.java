package aiven.zomboidhealthsystem.network.packet;

import aiven.zomboidhealthsystem.network.ServerPacketTypes;
import aiven.zomboidhealthsystem.health.Health;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

public final class ModHealthPacket implements FabricPacket {
    private final byte[] bytes;

    public ModHealthPacket(Health health) {
        this.bytes = health.getNbt();
    }

    public ModHealthPacket(byte[] bytes) {
        this.bytes = bytes;
    }

    public byte[] getBytes() {
        return bytes;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putByteArray("health", bytes);
        buf.writeNbt(compoundTag);
    }

    @Override
    public PacketType<?> getType() {
        return ServerPacketTypes.healthPacketType;
    }

    public static ModHealthPacket create(FriendlyByteBuf nbt) {
        try {
            CompoundTag compoundTag = nbt.readNbt();
            byte[] bytes = compoundTag.getByteArray("health");
            return new ModHealthPacket(bytes);
        } catch (Exception e) {
            return null;
        }
    }
}
