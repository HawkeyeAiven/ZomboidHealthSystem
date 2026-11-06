package aiven.zomboidhealthsystem.network;

import aiven.zomboidhealthsystem.ModItems;
import aiven.zomboidhealthsystem.ZomboidHealthSystem;
import aiven.zomboidhealthsystem.health.Health;
import aiven.zomboidhealthsystem.health.bodyparts.BodyPart;
import aiven.zomboidhealthsystem.item.AbstractBandageItem;
import aiven.zomboidhealthsystem.mixin.access.PlayerAccess;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public final class ServerPacketHandler {

    private ServerPacketHandler() {

    }

    public static void onActionPacket(ServerPlayer player, ActionType actionType, String bodyPartId, int index) {
        try {
            if (actionType == null || bodyPartId == null) {
                return;
            }
            Health health = ((PlayerAccess) player).zomboid_health_system$getModHealth();
            BodyPart bodyPart = health.getBodyPart(bodyPartId);
            if (bodyPart == null) {
                return;
            }
            if (actionType.equals(ActionType.BANDAGE)) {
                if (bodyPart.isBandaged()) {
                    return;
                }
                ItemStack itemStack = player.getInventory().getItem(index);
                if (itemStack.isEmpty()) {
                    return;
                }
                if (itemStack.getItem() instanceof AbstractBandageItem bandageItem) {
                    bodyPart.bandage(bandageItem);
                    itemStack.setCount(itemStack.getCount() - 1);
                } else {
                    return;
                }
            } else if (actionType.equals(ActionType.UNBANDAGE)) {
                if (!bodyPart.isBandaged()) {
                    return;
                }
                AbstractBandageItem bandageItem = bodyPart.unBandage();
                player.addItem(bandageItem.getDefaultInstance());
            } else if (actionType.equals(ActionType.DISINFECT)) {
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
    }
}
