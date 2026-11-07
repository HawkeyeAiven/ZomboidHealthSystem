package aiven.zomboidhealthsystem;

import aiven.zomboidhealthsystem.json5.Json5;
import aiven.zomboidhealthsystem.network.ServerNetworking;

import com.mojang.logging.LogUtils;

import net.minecraft.resources.ResourceLocation;

import org.slf4j.Logger;

public final class ZomboidHealthSystem {
    public static final String ID = "zomboidhealthsystem";
    public static final String NAME = "Zomboid Health System";
    public static final String VERSION = "2.0.3";
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

        //TODO идеи
        // - Показывать время только тогда, когда есть часы в инвентаре

        //TODO
        // - поддерживать моды, где ночь пролетает не сразу, а как в зомбоиде
    }

    public static ResourceLocation getResourceLocation(String path) {
        return new ResourceLocation(ID, path);
    }
}
