package aiven.zomboidhealthsystem.foundation.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public enum ServerPackets {
    DAMAGE(PacketIdentifiers.Client.DAMAGE, ClientTasks.DAMAGE),
    HEALTH(PacketIdentifiers.Client.HEALTH, ClientTasks.HEALTH),
    WORLD(PacketIdentifiers.Client.WORLD, ClientTasks.WORLD),
    DEATH(PacketIdentifiers.Client.RESPAWN, ClientTasks.RESPAWN)
    ;
    private final Identifier identifier;

    ServerPackets(Identifier identifier, ClientTasks task){
        this.identifier = identifier;
        ClientNetwork.registerPacket(identifier, task.get());
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    public static void initialize(){}
}