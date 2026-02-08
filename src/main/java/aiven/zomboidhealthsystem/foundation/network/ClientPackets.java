package aiven.zomboidhealthsystem.foundation.network;

import net.minecraft.util.Identifier;

public enum ClientPackets {
    BANDAGE(PacketIdentifiers.Server.BANDAGE, ServerTasks.BANDAGE),
    UNBANDAGE(PacketIdentifiers.Server.UNBANDAGE, ServerTasks.UNBANDAGE),
    CRAWL(PacketIdentifiers.Server.CRAWL, ServerTasks.CRAWL),
    DISINFECT(PacketIdentifiers.Server.DISINFECT, ServerTasks.DISINFECT)
    ;

    private final Identifier identifier;

    ClientPackets(Identifier identifier, ServerTasks task){
        this.identifier = identifier;
        ServerNetwork.registerPacket(identifier, task.get());
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    public static void initialize(){}
}
