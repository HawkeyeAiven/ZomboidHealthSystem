package aiven.zomboidhealthsystem.foundation.gui.widget;

import aiven.zomboidhealthsystem.foundation.client.ClientHealth;
import aiven.zomboidhealthsystem.foundation.items.BandageItem;
import aiven.zomboidhealthsystem.foundation.network.ClientNetwork;
import aiven.zomboidhealthsystem.foundation.network.PacketIdentifiers;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.text.Text;

public class BandageButton extends ModClickableWidget {
    private final ClientHealth.BodyPart bodyPart;
    private final BandageItem bandageItem;

    public BandageButton(int x, int y, int width, ClientHealth.BodyPart bodyPart, BandageItem bandageItem) {
        super(x, y, width, 20, Text.translatable("item.zomboidhealthsystem." + bandageItem));
        this.bodyPart = bodyPart;
        this.bandageItem = bandageItem;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        ClientNetwork.send(
                PacketIdentifiers.Server.BANDAGE,
                PacketByteBufs.create()
                        .writeString(bodyPart.getId())
                        .writeVarInt(BandageItem.getRawId(bandageItem))
        );
    }
}
