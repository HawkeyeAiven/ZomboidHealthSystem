package aiven.zomboidhealthsystem.foundation.gui.widget;

import aiven.zomboidhealthsystem.foundation.client.ClientHealth;
import aiven.zomboidhealthsystem.foundation.items.BandageItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.text.Text;

import java.util.ArrayList;

public class BandageContainer extends ButtonsContainer {
    public BandageContainer(int x, int y, ClientHealth.BodyPart bodyPart, ActionsButton actionsButton) {
        super(x, y, 60, 20, Text.of("Bandage"));
        PlayerInventory inventory = MinecraftClient.getInstance().player.getInventory();
        ArrayList<Integer> ids = new ArrayList<>();
        ArrayList<BandageItem> bandageItems = new ArrayList<>();

        for(int i = 0; i < inventory.size(); i++){
            if(inventory.getStack(i).getItem() instanceof BandageItem bandageItem){
                if(!ids.contains(Item.getRawId(bandageItem))) {
                    bandageItems.add(bandageItem);
                    ids.add(Item.getRawId(bandageItem));
                }
            }
        }
        int minWidth = 60;

        for(BandageItem bandageItem : bandageItems){
            minWidth = Math.max(MinecraftClient.getInstance().textRenderer.getWidth(Text.translatable("item.zomboidhealthsystem." + bandageItem)) + 6,minWidth);
        }

        for(BandageItem bandageItem : bandageItems){
            addButton(new BandageButton(0,0, minWidth, bodyPart, bandageItem, actionsButton));
        }
    }
}
