package aiven.zomboidhealthsystem.foundation.gui.widget;

import aiven.zomboidhealthsystem.ModItems;
import aiven.zomboidhealthsystem.ZomboidHealthSystemClient;
import aiven.zomboidhealthsystem.foundation.gui.screen.UIHealth;
import aiven.zomboidhealthsystem.foundation.items.AlcoholWipe;
import aiven.zomboidhealthsystem.foundation.items.BandageItem;
import aiven.zomboidhealthsystem.foundation.network.ClientNetwork;
import aiven.zomboidhealthsystem.foundation.network.ClientPackets;
import aiven.zomboidhealthsystem.foundation.utility.Util;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class ActionsButton extends TemporalButtonsContainer {
    private final UIHealth screen;

    public ActionsButton(int x, int y, AbstractModScrollableWidget.Element element, UIHealth screen) {
        super(x, y, 60, 20, Text.of("Actions ->"));
        this.screen = screen;
        PlayerInventory inv = MinecraftClient.getInstance().player.getInventory();
        ClientPlayerEntity user = MinecraftClient.getInstance().player;
        Item bandageItem = Util.getCooldowningBandageItem(user);
        if(bandageItem == null) {
            addBandageButton(element);
        } else {
            TimerButton timerButton = new TimerButton(0,0,60,20,Util.getCooldownTime(bandageItem,BandageItem.BANDAGE_COOLDOWN_TIME,5) * 50,null);
            timerButton.setTask((x1,y1,button) -> {
                addBandageButton(element);
                this.removeButton(timerButton);
            });
            addButton(timerButton);
        }
        boolean bl = user.getItemCooldownManager().isCoolingDown(ModItems.ALCOHOL_WIPE);
        int index = screen.zomboidWidget.elements.indexOf(element);
        if (ZomboidHealthSystemClient.HEALTH.indexOf(index).isInfection() && Util.inventoryContains(inv,ModItems.ALCOHOL_WIPE)) {
            if(!bl){
                addDisinfectButton(element);
            } else {
                TimerButton timerButton = new TimerButton(0,0,60,20,Util.getCooldownTime(ModItems.ALCOHOL_WIPE, AlcoholWipe.DISINFECT_COOLDOWN_TIME,5) * 50,null);
                timerButton.setTask(((x1, y1, button) -> {
                    addDisinfectButton(element);
                    this.removeButton(timerButton);
                }));
                addButton(timerButton);
            }
        }
    }

    public void addDisinfectButton(AbstractModScrollableWidget.Element element){
        addButton(new ModButton(0,0,60,20,Text.of("Disinfect"),(x1,y1,button) -> {
            ClientNetwork.send(ClientPackets.DISINFECT.getIdentifier(), PacketByteBufs.create().writeString(element.getName()));
            this.destroy();
        }));
    }

    public void addBandageButton(AbstractModScrollableWidget.Element element){
        PlayerInventory inv = MinecraftClient.getInstance().player.getInventory();
        int index = screen.zomboidWidget.elements.indexOf(element);
        if (!ZomboidHealthSystemClient.HEALTH.indexOf(index).isBandaged()) {
            for(int i = 0; i < inv.size(); i++){
                if(inv.getStack(i).getItem() instanceof BandageItem){
                    addButton(new BandageContainer(this, element.getName()));
                    break;
                }
            }
        } else {
            addButton(new ModButton(0,0,60,20,Text.of("Unbandage"),(x1,y1,button) ->{
                ClientNetwork.send(ClientPackets.UNBANDAGE.getIdentifier(), PacketByteBufs.create().writeString(element.getName()));
                this.destroy();
            }));
        }
    }
}
