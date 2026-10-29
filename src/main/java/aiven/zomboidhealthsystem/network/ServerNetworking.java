package aiven.zomboidhealthsystem.network;


import aiven.zomboidhealthsystem.ModItems;
import aiven.zomboidhealthsystem.ZomboidHealthSystem;
import aiven.zomboidhealthsystem.item.AbstractBandageItem;
import aiven.zomboidhealthsystem.mixin.access.PlayerAccess;
import aiven.zomboidhealthsystem.network.packet.ActionPacket;
import aiven.zomboidhealthsystem.health.Health;
import aiven.zomboidhealthsystem.health.bodyparts.BodyPart;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public final class ServerNetworking {

    private ServerNetworking() {

    }

    public static void send(ServerPlayer player, FabricPacket packet) {
        ServerPlayNetworking.send(player, packet);
    }

    public static <T extends FabricPacket> PacketType<T> register(PacketType<T> type, ServerPlayNetworking.PlayPacketHandler<T> handler) {
        ServerPlayNetworking.registerGlobalReceiver(type, handler);
        return type;
    }

    public static void initialize() {
        register(ClientPacketTypes.actionPacketType, (packet, player, responseSender) -> {
            try {
                if (packet.getActionType() == null || packet.getBodyPartId() == null) {
                    return;
                }
                Health health = ((PlayerAccess) player).zomboid_health_system$getModHealth();
                BodyPart bodyPart = health.getBodyPart(packet.getBodyPartId());
                if (bodyPart == null) {
                    return;
                }
                if (packet.getActionType().equals(ActionPacket.Type.BANDAGE)) {
                    if (bodyPart.isBandaged()) {
                        return;
                    }
                    ItemStack itemStack = player.getInventory().getItem(packet.getIndex());
                    if (itemStack.isEmpty()) {
                        return;
                    }
                    if (itemStack.getItem() instanceof AbstractBandageItem bandageItem) {
                        bodyPart.bandage(bandageItem);
                        itemStack.setCount(itemStack.getCount() - 1);
                    } else {
                        return;
                    }
                } else if (packet.getActionType().equals(ActionPacket.Type.UNBANDAGE)) {
                    if (!bodyPart.isBandaged()) {
                        return;
                    }
                    AbstractBandageItem bandageItem = bodyPart.unBandage();
                    player.addItem(bandageItem.getDefaultInstance());
                } else if (packet.getActionType().equals(ActionPacket.Type.DISINFECT)) {
                    if (!bodyPart.hasInfection()) {
                        return;
                    }
                    for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                        if (player.getInventory().getItem(i).getItem().equals(ModItems.ALCOHOL_WIPE)) {
                            ItemStack itemStack = player.getInventory().getItem(i);
                            itemStack.setDamageValue(itemStack.getDamageValue() + 1);
                            if (itemStack.getDamageValue() > itemStack.getMaxDamage()) {
                                itemStack.setCount(itemStack.getCount() - 1);
                            }
                            bodyPart.disInfect();
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                ZomboidHealthSystem.log.error("Failed to execute actionPacketType from {}", player.getName().getString(), e);
            }
        });
    }
}
