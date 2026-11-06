package aiven.zomboidhealthsystem.network.forge;

import aiven.zomboidhealthsystem.network.forge.packet.HealthPacket;
import aiven.zomboidhealthsystem.network.ClientPacketHandler;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

public final class ClientNetworkingImpl {

    private ClientNetworkingImpl() {

    }

    public static void send(Object packet) {
        ModChannel.getInstance().sendToServer(packet);
    }

    public static void initialize() {
        ModChannel.getInstance().registerMessage(0, HealthPacket.class, HealthPacket::write, HealthPacket::create, (packet, ctx) -> {
            ctx.get().enqueueWork(() -> {
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandler.onHealthPacket(packet.getBytes()));
            });
            ctx.get().setPacketHandled(true);
        });
    }
}
