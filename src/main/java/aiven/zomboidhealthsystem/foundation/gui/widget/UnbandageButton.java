package aiven.zomboidhealthsystem.foundation.gui.widget;

import aiven.zomboidhealthsystem.foundation.client.ClientHealth;
import aiven.zomboidhealthsystem.foundation.network.ClientNetwork;
import aiven.zomboidhealthsystem.foundation.network.PacketIdentifiers;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.text.Text;

public class UnbandageButton extends ModClickableWidget {
    private final ClientHealth.BodyPart bodyPart;

    public UnbandageButton(int x, int y, ClientHealth.BodyPart bodyPart) {
        super(x, y, 60, 20, Text.of("Unbandage"));
        this.bodyPart = bodyPart;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        ClientNetwork.send(PacketIdentifiers.Server.UNBANDAGE, PacketByteBufs.create().writeString(bodyPart.getId()));
    }
}
