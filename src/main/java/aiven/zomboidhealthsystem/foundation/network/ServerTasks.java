package aiven.zomboidhealthsystem.foundation.network;

import aiven.zomboidhealthsystem.Config;
import aiven.zomboidhealthsystem.ModItems;
import aiven.zomboidhealthsystem.ZomboidHealthSystem;
import aiven.zomboidhealthsystem.foundation.items.AlcoholWipe;
import aiven.zomboidhealthsystem.foundation.world.ModServer;
import aiven.zomboidhealthsystem.foundation.utility.Util;
import aiven.zomboidhealthsystem.foundation.items.BandageItem;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public enum ServerTasks {
    BANDAGE((minecraftServer, serverPlayerEntity, serverPlayNetworkHandler, packetByteBuf, packetSender) -> {
        try {
            String bodyPart = packetByteBuf.readString();
            int type = packetByteBuf.readVarInt();

            PlayerInventory inventory = serverPlayerEntity.getInventory();

            BandageItem bandageItem = (BandageItem) Item.byRawId(type);

            inventory.getStack(inventory.getSlotWithStack(bandageItem.getDefaultStack())).decrement(1);

            Util.setCooldownAllBandageItems(serverPlayerEntity.getItemCooldownManager(),BandageItem.BANDAGE_COOLDOWN_TIME);

            ModServer.getHealth(serverPlayerEntity).getBodyPart(bodyPart).bandage(bandageItem);

            ModServer.sendPacketHealth(serverPlayerEntity);
        } catch (Exception e){

            print(
                    ServerTasks.EXCEPTION
                    .formatted(
                            e,
                            ClientPackets.BANDAGE.getIdentifier().getPath(),
                            serverPlayerEntity.getName().getString()
                    )
            );
        }

    }),
    UNBANDAGE((minecraftServer, serverPlayerEntity, serverPlayNetworkHandler, packetByteBuf, packetSender) -> {
        try {

            if(ModServer.getHealth(serverPlayerEntity) != null){
                String bodyPart = packetByteBuf.readString();

                Item bandageItem = ModServer.getHealth(serverPlayerEntity).getBodyPart(bodyPart).unBandage();

                if(!serverPlayerEntity.giveItemStack(bandageItem.getDefaultStack())) {
                    serverPlayerEntity.dropStack(bandageItem.getDefaultStack());
                }

                Util.setCooldownAllBandageItems(serverPlayerEntity.getItemCooldownManager(), BandageItem.UNBANDAGE_COOLDOWN_TIME);

                ModServer.sendPacketHealth(serverPlayerEntity);
            }
        } catch (Exception e){

            print(
                    ServerTasks.EXCEPTION
                    .formatted(
                            e,
                            ClientPackets.UNBANDAGE.getIdentifier().getPath(),
                            serverPlayerEntity.getName().getString()
                    )
            );

    }
    }),
    CRAWL((minecraftServer, serverPlayerEntity, serverPlayNetworkHandler, packetByteBuf, packetSender) -> {
        try {
            EntityPose pose;
            int time;

            if (!serverPlayerEntity.isCrawling()) {
                pose = EntityPose.SWIMMING;
                time = 200;
            } else if (ModServer.getHealth(serverPlayerEntity).isCanWalk() && serverPlayerEntity.getWorld().getBlockState(serverPlayerEntity.getBlockPos().up(1)).getBlock().getBlastResistance() == 0) {
                pose = EntityPose.STANDING;
                time = 700;
            } else {
                return;
            }
            if(Config.DELAY_BEFORE_CRAWLING.getValue()) {
                new Thread(() -> {
                    try {
                        Thread.sleep(time);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    serverPlayerEntity.setPose(pose);
                }).start();
            } else {
                serverPlayerEntity.setPose(pose);
            }
        } catch (Exception e){

            print(
                    ServerTasks.EXCEPTION
                    .formatted(
                            e,
                            ClientPackets.CRAWL.getIdentifier().getPath(),
                            serverPlayerEntity.getName().getString()
                    )
            );

        }
    }),
    DISINFECT((minecraftServer, serverPlayerEntity, serverPlayNetworkHandler, packetByteBuf, packetSender) -> {
        try {
            String bodyPart = packetByteBuf.readString();

            ModServer.getHealth(serverPlayerEntity).getBodyPart(bodyPart).disInfect();

            PlayerInventory inv = serverPlayerEntity.getInventory();
            for (int i = 0; i < inv.size(); i++) {
                ItemStack item = inv.getStack(i);
                if (item.getItem().equals(ModItems.ALCOHOL_WIPE)) {
                    item.setDamage(item.getDamage() + 1);
                    if (item.getDamage() > item.getMaxDamage()) {
                        item.decrement(1);
                    }
                    break;
                }
            }
            serverPlayerEntity.getItemCooldownManager().set(ModItems.ALCOHOL_WIPE, AlcoholWipe.DISINFECT_COOLDOWN_TIME);

            ModServer.sendPacketHealth(serverPlayerEntity);
        } catch (Exception e){

            print(
                    ServerTasks.EXCEPTION
                    .formatted(
                            e,
                            ClientPackets.DISINFECT.getIdentifier().getPath(),
                            serverPlayerEntity.getName().getString()
                    )
            );

        }

    })
    ;

    private static final String EXCEPTION =
            "\n" + ZomboidHealthSystem.NAME + ":" + """
            
                %s in
                    Packet: %s
                    Player: %s
            """;

    private final ServerPlayNetworking.PlayChannelHandler task;

    ServerTasks(ServerPlayNetworking.PlayChannelHandler task){
        this.task=task;
    }

    public ServerPlayNetworking.PlayChannelHandler get(){
        return this.task;
    }

    public static void print(String string){
        ZomboidHealthSystem.LOGGER.info(string);
    }

    public static void initialize(){}
}