package aiven.zomboidhealthsystem.foundation.gui.widget;

import aiven.zomboidhealthsystem.foundation.items.AlcoholWipe;
import aiven.zomboidhealthsystem.foundation.network.ClientNetwork;
import aiven.zomboidhealthsystem.foundation.network.PacketIdentifiers;
import aiven.zomboidhealthsystem.foundation.player.bodyparts.BodyPart;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.text.Text;

public class DisinfectButton extends ModButtonWithDelay {
    private final BodyPart bodyPart;
    private final ActionsButton actionsButton;

    public DisinfectButton(int x, int y, BodyPart bodyPart, ActionsButton actionsButton) {
        super(x, y, 60, 20, AlcoholWipe.DISINFECT_COOLDOWN_TIME, Text.of("Disinfect"));
        this.bodyPart = bodyPart;
        this.actionsButton = actionsButton;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        ClientNetwork.send(PacketIdentifiers.Server.DISINFECT, PacketByteBufs.create().writeString(bodyPart.getId()));
        this.actionsButton.destroy();
        super.onClick(mouseX, mouseY);
    }
}
