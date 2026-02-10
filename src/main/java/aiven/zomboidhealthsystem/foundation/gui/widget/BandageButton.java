package aiven.zomboidhealthsystem.foundation.gui.widget;

import aiven.zomboidhealthsystem.foundation.client.ClientHealth;
import aiven.zomboidhealthsystem.foundation.items.BandageItem;
import aiven.zomboidhealthsystem.foundation.network.ClientNetwork;
import aiven.zomboidhealthsystem.foundation.network.PacketIdentifiers;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.text.Text;

public class BandageButton extends ModButtonWithDelay {
    private final ClientHealth.BodyPart bodyPart;
    private final BandageItem bandageItem;
    private final ActionsButton actionsButton;

    public BandageButton(int x, int y, int width, ClientHealth.BodyPart bodyPart, BandageItem bandageItem, ActionsButton actionsButton) {
        super(x, y, width, 20, BandageItem.BANDAGE_COOLDOWN_TIME, Text.translatable("item.zomboidhealthsystem." + bandageItem));
        this.bodyPart = bodyPart;
        this.bandageItem = bandageItem;
        this.actionsButton = actionsButton;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        ClientNetwork.send(
                PacketIdentifiers.Server.BANDAGE,
                PacketByteBufs.create()
                        .writeString(bodyPart.getId())
                        .writeVarInt(BandageItem.getRawId(bandageItem))
        );
        this.actionsButton.destroy();
        super.onClick(mouseX, mouseY);
    }
}
