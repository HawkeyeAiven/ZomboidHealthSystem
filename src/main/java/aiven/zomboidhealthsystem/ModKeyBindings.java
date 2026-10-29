package aiven.zomboidhealthsystem;

import aiven.zomboidhealthsystem.gui.screen.ModScreen;

import com.mojang.blaze3d.platform.InputConstants;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

import org.lwjgl.glfw.GLFW;

public final class ModKeyBindings {

    public static final KeyMapping HEALTH = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.zomboid-health-system.overlay",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_H,
            "key.kategory.zomboid-health-system.zomboid"
    ));

    private ModKeyBindings() {

    }

    public static void health() {
        Minecraft.getInstance().setScreen(new ModScreen());
    }

    public static void initialize() {

    }
}
