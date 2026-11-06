package aiven.zomboidhealthsystem.network.forge.packet;

import aiven.zomboidhealthsystem.health.Health;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

public final class HealthPacket {
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

    public void write(FriendlyByteBuf buf) {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putByteArray("health", bytes);
        buf.writeNbt(compoundTag);
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
