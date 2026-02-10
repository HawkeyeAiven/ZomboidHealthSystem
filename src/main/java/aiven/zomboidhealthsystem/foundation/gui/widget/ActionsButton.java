package aiven.zomboidhealthsystem.foundation.gui.widget;

import aiven.zomboidhealthsystem.ModItems;
import aiven.zomboidhealthsystem.foundation.client.ClientHealth;
import aiven.zomboidhealthsystem.foundation.utility.Util;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

public class ActionsButton extends TemporalButtonsContainer {
    public ActionsButton(int x, int y, ClientHealth.BodyPart bodyPart) {
        super(x, y, 60, 20, Text.of("Actions ->"));
        ClientPlayerEntity user = MinecraftClient.getInstance().player;
        PlayerInventory inventory = user.getInventory();

        if(!bodyPart.isBandaged()) {
            if(Util.getBandageItem(inventory) != null) {
                addButton(new BandageContainer(0, 0, bodyPart));
            }
        } else {
            addButton(new UnbandageButton(0,0,bodyPart));
        }

        if(bodyPart.isInfection() && Util.inventoryContains(inventory, ModItems.ALCOHOL_WIPE)) {
            addButton(new DisinfectButton(0,0,bodyPart));
        }
    }
}
