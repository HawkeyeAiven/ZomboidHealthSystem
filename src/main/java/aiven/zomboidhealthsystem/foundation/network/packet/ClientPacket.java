package aiven.zomboidhealthsystem.foundation.network.packet;

import aiven.zomboidhealthsystem.ZomboidHealthSystem;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

public class ClientPacket {
    public final Identifier ID;
    public final ServerPlayNetworking.PlayChannelHandler HANDLER;

    public ClientPacket(String id, ServerPlayNetworking.PlayChannelHandler HANDLER){
        ID = new Identifier(ZomboidHealthSystem.ID,id);
        this.HANDLER=HANDLER;
    }
}
