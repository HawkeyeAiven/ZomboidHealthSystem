package aiven.zomboidhealthsystem.foundation.gui.widget;

import aiven.zomboidhealthsystem.foundation.client.ClientHealth;
import aiven.zomboidhealthsystem.foundation.network.ClientNetwork;
import aiven.zomboidhealthsystem.foundation.network.PacketIdentifiers;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.text.Text;

public class DisinfectButton extends ModClickableWidget {
    private final ClientHealth.BodyPart bodyPart;

    public DisinfectButton(int x, int y, ClientHealth.BodyPart bodyPart) {
        super(x, y, 60, 20, Text.of("Disinfect"));
        this.bodyPart = bodyPart;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        ClientNetwork.send(PacketIdentifiers.Server.DISINFECT, PacketByteBufs.create().writeString(bodyPart.getId()));
    }
}
