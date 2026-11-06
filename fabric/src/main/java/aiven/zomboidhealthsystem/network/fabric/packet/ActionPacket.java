package aiven.zomboidhealthsystem.network.fabric.packet;

import aiven.zomboidhealthsystem.network.fabric.ClientPacketTypes;
import aiven.zomboidhealthsystem.network.ActionType;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

public final class ActionPacket implements FabricPacket {

    private final ActionType actionType;
    private final String bodyPartId;
    private final int index;

    public ActionPacket(ActionType actionType, String bodyPartId, int index) {
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

    public ActionType getActionType() {
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
            ActionType type = ActionType.valueOf(nbt.getString("type"));
            String bodyPartId = nbt.getString("bodyPartId");
            int index = nbt.getInt("index");
            return new ActionPacket(type, bodyPartId, index);
        } catch (Exception e) {
            return null;
        }
    }
}
