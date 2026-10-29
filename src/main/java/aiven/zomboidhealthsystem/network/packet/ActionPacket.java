package aiven.zomboidhealthsystem.network.packet;

import aiven.zomboidhealthsystem.network.ClientPacketTypes;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

public final class ActionPacket implements FabricPacket {

    private final Type actionType;
    private final String bodyPartId;
    private final int index;

    public ActionPacket(Type actionType, String bodyPartId, int index) {
        this.actionType = actionType;
        this.bodyPartId = bodyPartId;
        this.index = index;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        CompoundTag nbt = new CompoundTag();
        nbt.putString("type", actionType.toString());
        nbt.putString("bodyPartId", bodyPartId);
        nbt.putInt("index", index);
        buf.writeNbt(nbt);
    }

    @Override
    public PacketType<?> getType() {
        return ClientPacketTypes.actionPacketType;
    }

    public Type getActionType() {
        return actionType;
    }

    public String getBodyPartId() {
        return bodyPartId;
    }

    public int getIndex() {
        return index;
    }

    public static ActionPacket create(FriendlyByteBuf buf) {
        try {
            CompoundTag nbt = buf.readNbt();
            Type type = Type.valueOf(nbt.getString("type"));
            String bodyPartId = nbt.getString("bodyPartId");
            int index = nbt.getInt("index");
            return new ActionPacket(type, bodyPartId, index);
        } catch (Exception e) {
            return null;
        }
    }

    public enum Type {
        BANDAGE,
        UNBANDAGE,
        DISINFECT
    }
}
