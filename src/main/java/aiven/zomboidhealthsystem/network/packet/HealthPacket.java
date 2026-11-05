package aiven.zomboidhealthsystem.network.packet;

import aiven.zomboidhealthsystem.network.ServerPacketTypes;
import aiven.zomboidhealthsystem.health.Health;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

public final class HealthPacket implements FabricPacket {
    private final byte[] bytes;

    public HealthPacket(Health health) {
        this.bytes = health.getNbt();
    }

    public HealthPacket(byte[] bytes) {
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

    public static HealthPacket create(FriendlyByteBuf nbt) {
        try {
            CompoundTag compoundTag = nbt.readNbt();
            byte[] bytes = compoundTag.getByteArray("health");
            return new HealthPacket(bytes);
        } catch (Exception e) {
            return null;
        }
    }
}
