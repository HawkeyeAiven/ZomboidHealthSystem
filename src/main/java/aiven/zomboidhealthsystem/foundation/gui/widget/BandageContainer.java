package aiven.zomboidhealthsystem.foundation.gui.widget;

import aiven.zomboidhealthsystem.foundation.gui.screen.AbstractModScreen;
import aiven.zomboidhealthsystem.foundation.items.BandageItem;
import aiven.zomboidhealthsystem.foundation.network.ClientNetwork;
import aiven.zomboidhealthsystem.foundation.network.ClientPackets;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Stack;

@Environment(EnvType.CLIENT)
public class BandageContainer extends ButtonsContainer {
    ActionsButton actionsButton;

    public BandageContainer(ActionsButton actionsButton, String bodyPart) {
        super(0,0, 60, 20, Text.of("Bandage"));
        this.actionsButton = actionsButton;

        PlayerInventory inventory = MinecraftClient.getInstance().player.getInventory();

        ArrayList<Integer> ids = new ArrayList<>();
        ArrayList<BandageItem> bandageItems = new ArrayList<>();

        for(int i = 0; i < 36; i++){
            if(inventory.getStack(i).getItem() instanceof BandageItem bandageItem){
                if(!ids.contains(Item.getRawId(bandageItem))) {
                    bandageItems.add(bandageItem);
                    ids.add(Item.getRawId(bandageItem));
                }
            }
        }
        int maxWidth = 60;

        for(BandageItem bandageItem : bandageItems){
            maxWidth = Math.max(MinecraftClient.getInstance().textRenderer.getWidth(Text.translatable("item.zomboidhealthsystem." + bandageItem)) + 6,maxWidth);
        }

        for(BandageItem bandageItem : bandageItems){
            addButton(new ModButton(0,0,maxWidth,20,Text.translatable("item.zomboidhealthsystem." + bandageItem),(x,y,button) ->{
                ClientNetwork.send(
                        ClientPackets.BANDAGE.getIdentifier(),
                        PacketByteBufs.create()
                                .writeString(bodyPart)
                                .writeVarInt(Item.getRawId(bandageItem))

                );
                if(MinecraftClient.getInstance().currentScreen instanceof AbstractModScreen screen){
                    screen.destroy(this.actionsButton);
                }
            }));
        }
    }
}