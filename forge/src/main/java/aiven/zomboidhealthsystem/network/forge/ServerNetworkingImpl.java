package aiven.zomboidhealthsystem.network.forge;

import aiven.zomboidhealthsystem.network.forge.packet.ActionPacket;
import aiven.zomboidhealthsystem.network.ServerPacketHandler;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;


public final class ServerNetworkingImpl {

    private ServerNetworkingImpl() {

    }

    public static void send(ServerPlayer player, Object packet) {
        ModChannel.getInstance().send(PacketDistributor.PLAYER.with(() -> player), packet);
    }

    public static void initialize() {
        ModChannel.getInstance().registerMessage(1, ActionPacket.class, ActionPacket::write, ActionPacket::create, (packet, ctx) -> {
            ctx.get().enqueueWork(() -> {
                ServerPlayer player = ctx.get().getSender();
                ServerPacketHandler.onActionPacket(player, packet.getActionType(), packet.getBodyPartId(), packet.getIndex());
            });
            ctx.get().setPacketHandled(true);
        });
    }
}
