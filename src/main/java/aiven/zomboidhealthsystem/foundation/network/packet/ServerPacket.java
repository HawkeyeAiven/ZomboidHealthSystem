package aiven.zomboidhealthsystem.foundation.network.packet;

import aiven.zomboidhealthsystem.ZomboidHealthSystem;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.Identifier;

public class ServerPacket {
    public final Identifier ID;
    public final ClientPlayNetworking.PlayChannelHandler HANDLER;

    public ServerPacket(String id, ClientPlayNetworking.PlayChannelHandler HANDLER){
        ID = new Identifier(ZomboidHealthSystem.ID,id);
        this.HANDLER=HANDLER;
    }
}
