package aiven.zomboidhealthsystem.foundation.network;

import aiven.zomboidhealthsystem.ZomboidHealthSystem;
import net.minecraft.util.Identifier;

public class PacketIdentifiers {
    private static Identifier create(String name) {
        return new Identifier(ZomboidHealthSystem.ID, name);
    }

    public static class Client {
        public static final Identifier DAMAGE = create("damage");
        public static final Identifier HEALTH = create("health");
        public static final Identifier WORLD = create("world");
        public static final Identifier RESPAWN = create("respawn");
    }

    public static class Server {
        public static final Identifier BANDAGE = create("bandage");
        public static final Identifier UNBANDAGE = create("unbandage");
        public static final Identifier CRAWL = create("crawl");
        public static final Identifier DISINFECT = create("disinfect");
    }
}