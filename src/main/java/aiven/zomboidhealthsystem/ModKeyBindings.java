package aiven.zomboidhealthsystem;

import aiven.zomboidhealthsystem.foundation.gui.screen.UI;
import aiven.zomboidhealthsystem.foundation.gui.screen.UIInfo;
import aiven.zomboidhealthsystem.foundation.network.ClientNetwork;
import aiven.zomboidhealthsystem.foundation.gui.screen.UIHealth;
import aiven.zomboidhealthsystem.foundation.network.ClientPackets;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class ModKeyBindings {
    public static final KeyBinding HEALTH = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.zomboidhealthsystem.overlay",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_H,
            "key.kategory.zomboidhealthsystem.zomboid"
    ));
    public static final KeyBinding CRAWL = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.zomboidhealthsystem.crawl",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_Z,
            "key.kategory.zomboidhealthsystem.zomboid"
    ));

    public static void health(){
        if(UI.LAST_UI instanceof UIInfo) {
            MinecraftClient.getInstance().setScreen(new UIInfo(UI.LAST_POS));
        } else {
            MinecraftClient.getInstance().setScreen(new UIHealth(UI.LAST_POS));
        }
    }

    public static void crawl(){
        ClientNetwork.send(ClientPackets.CRAWL.getIdentifier(), PacketByteBufs.create());
    }

    public static void initialize(){}
}
