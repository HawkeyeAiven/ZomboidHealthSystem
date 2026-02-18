package aiven.zomboidhealthsystem.foundation.gui.widget;

import aiven.zomboidhealthsystem.foundation.items.BandageItem;
import aiven.zomboidhealthsystem.foundation.network.ClientNetwork;
import aiven.zomboidhealthsystem.foundation.network.PacketIdentifiers;
import aiven.zomboidhealthsystem.foundation.player.bodyparts.BodyPart;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.text.Text;

public class UnbandageButton extends ModButtonWithDelay {
    private final BodyPart bodyPart;
    private final ActionsButton actionsButton;

    public UnbandageButton(int x, int y, BodyPart bodyPart, ActionsButton actionsButton) {
        super(x, y, 60, 20, BandageItem.UNBANDAGE_COOLDOWN_TIME, Text.of("Unbandage"));
        this.bodyPart = bodyPart;
        this.actionsButton = actionsButton;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        ClientNetwork.send(PacketIdentifiers.Server.UNBANDAGE, PacketByteBufs.create().writeString(bodyPart.getId()));
        actionsButton.destroy();
        super.onClick(mouseX, mouseY);
    }
}
