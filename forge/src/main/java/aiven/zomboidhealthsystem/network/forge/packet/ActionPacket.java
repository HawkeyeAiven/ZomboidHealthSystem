package aiven.zomboidhealthsystem.network.forge.packet;

import aiven.zomboidhealthsystem.network.ActionType;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

public final class ActionPacket {

    private final ActionType actionType;
    private final String bodyPartId;
    private final int index;

    public ActionPacket(ActionType actionType, String bodyPartId, int index) {
        this.actionType = actionType;
        this.bodyPartId = bodyPartId;
        this.index = index;
    }

    public void write(FriendlyByteBuf buf) {
        CompoundTag nbt = new CompoundTag();
        nbt.putString("type", actionType.toString());
        nbt.putString("bodyPartId", bodyPartId);
        nbt.putInt("index", index);
        buf.writeNbt(nbt);
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