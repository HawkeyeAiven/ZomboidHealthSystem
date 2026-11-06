package aiven.zomboidhealthsystem;

import aiven.zomboidhealthsystem.gui.screen.ModScreen;

import com.mojang.blaze3d.platform.InputConstants;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

import org.lwjgl.glfw.GLFW;

public final class ModKeyBindings {

    public static final KeyMapping HEALTH = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key." + ZomboidHealthSystem.ID + ".overlay",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_H,
            "key.kategory." + ZomboidHealthSystem.ID + ".zomboid"
    ));

    private ModKeyBindings() {

    }

    public static void health() {
        Minecraft.getInstance().setScreen(new ModScreen());
    }

    public static void initialize() {

    }
}
