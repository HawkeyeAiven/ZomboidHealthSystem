package aiven.zomboidhealthsystem;

import aiven.zomboidhealthsystem.json5.Json5;
import aiven.zomboidhealthsystem.network.ServerNetworking;

import com.mojang.logging.LogUtils;

import net.minecraft.resources.ResourceLocation;

import org.slf4j.Logger;

public final class ZomboidHealthSystem {
    public static final String ID = "zomboidhealthsystem";
    public static final String NAME = "Zomboid Health System";
    public static final String VERSION = "2.0.2";
    public static final String ID_VER = ID + "-" + VERSION;

    public static final Logger log = LogUtils.getLogger();

    private ZomboidHealthSystem() {

    }
    
    public static void initialize() {
        Json5.initialize();
        Config.initialize();

        ModDamageTypes.initialize();
        ModItems.initialize();
        ModItemGroups.initialize();
        ServerNetworking.initialize();
    }

    public static ResourceLocation getResourceLocation(String path) {
        return new ResourceLocation(ID, path);
    }
}
