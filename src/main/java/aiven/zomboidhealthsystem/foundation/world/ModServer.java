package aiven.zomboidhealthsystem.foundation.world;

import aiven.zomboidhealthsystem.Config;
import aiven.zomboidhealthsystem.foundation.network.PacketIdentifiers;
import aiven.zomboidhealthsystem.foundation.network.ServerNetwork;
import aiven.zomboidhealthsystem.foundation.player.Health;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

public class ModServer {
    public static WorldSettings WORLD_SETTINGS_ON_CREATING_WORLD = Config.DEFAULT_WORLD_SETTINGS.getValue();

    public static final ConcurrentHashMap<PlayerEntity, Health> MAP = new ConcurrentHashMap<>();
    public static Weather WEATHER;
    public static WorldSettings WORLD_SETTINGS;

    public static void sendPacketHealth(ServerPlayerEntity player) {
        Health health = getHealth(player);
        if(health != null) {
            ServerNetwork.send(PacketIdentifiers.Client.HEALTH,
                    player,
                    PacketByteBufs.create()
                            .writeString(health.toString()));
        }

    }

    public static void sendPacketDamage(ServerPlayerEntity player){
        ServerNetwork.send(PacketIdentifiers.Client.DAMAGE, player, PacketByteBufs.create());
    }

    public static void sendPacketWorld(PlayerEntity player) {
        if(WEATHER.getWorld().equals(player.getWorld())) {
            int[] array = new int[5];

            array[0] = (int) (WEATHER.getTemperatureAtPos(player.getBlockPos()) * 10);
            array[1] = (int) (WEATHER.getWorldTemperature() * 10);
            array[2] = (int) (WEATHER.getSeasonTemperature() * 10);
            array[3] = (int) (WEATHER.getWind() * 10);
            array[4] = WORLD_SETTINGS.getDayLengthMultiplier();


            PacketByteBuf packet = PacketByteBufs.create();
            packet.writeIntArray(array);
            packet.writeBoolean(WORLD_SETTINGS.hasTemperature());

            ServerNetwork.send(
                    PacketIdentifiers.Client.WORLD,
                    (ServerPlayerEntity) player,
                    packet
            );
        }
    }

    public static void sendPacketRespawn(ServerPlayerEntity player) {
        ServerNetwork.send(PacketIdentifiers.Client.RESPAWN, player, PacketByteBufs.create());
    }

    public static void registerPlayer(PlayerEntity player, Health health) {
        Enumeration<PlayerEntity> list = MAP.keys();
        while (list.hasMoreElements()) {
            PlayerEntity user = list.nextElement();
            if(user.getEntityName().equals(player.getEntityName())) {
                MAP.remove(user);
            }
        }
        MAP.put(player,health);
    }

    public static Health getHealth(PlayerEntity player) {
        return MAP.get(player);
    }

    public static Enumeration<PlayerEntity> getRegisteredPlayers() {
        return MAP.keys();
    }
}
