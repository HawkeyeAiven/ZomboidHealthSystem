package aiven.zomboidhealthsystem;

import aiven.zomboidhealthsystem.gui.screen.ModScreen;

import com.mojang.blaze3d.platform.InputConstants;

import dev.architectury.injectables.annotations.ExpectPlatform;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

import org.lwjgl.glfw.GLFW;

import java.util.HashMap;

public final class ModKeyBindings {

    public static final HashMap<KeyMapping, Runnable> keyMappings = new HashMap<>();

    public static final KeyMapping HEALTH = register(new KeyMapping(
            "key." + ZomboidHealthSystem.ID + ".overlay",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_H,
            "key.kategory." + ZomboidHealthSystem.ID + ".zomboid"
    ), () -> Minecraft.getInstance().setScreen(new ModScreen()));

    private ModKeyBindings() {

    }

    private static KeyMapping register(KeyMapping keyMapping, Runnable runnable) {
        keyMappings.put(keyMapping, runnable);
        return keyMapping;
    }

    @ExpectPlatform
    public static void initialize() {
        throw new AssertionError();
    }
}
