package aiven.zomboidhealthsystem.fabric;

import aiven.zomboidhealthsystem.ModKeyBindings;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

import net.minecraft.client.KeyMapping;

public final class ModKeyBindingsImpl {

    private ModKeyBindingsImpl() {

    }

    public static void initialize() {
        for(KeyMapping keyMapping : ModKeyBindings.keyMappings.keySet()) {
            KeyBindingHelper.registerKeyBinding(keyMapping);
        }
    }
}
