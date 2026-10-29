package aiven.zomboidhealthsystem;

import aiven.zomboidhealthsystem.network.ServerNetworking;

import net.fabricmc.api.ModInitializer;

import net.minecraft.resources.ResourceLocation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public final class ZomboidHealthSystem implements ModInitializer {
	public static final String ID = "zomboid-health-system";
	public static final String NAME = "Zomboid Health System";
	public static final String VERSION = "2.0.0";
	public static final String ID_VER = ID + "-" + VERSION;

	public static final Logger log = LoggerFactory.getLogger(NAME);

	@Override
	public void onInitialize() {
		Config.initialize();
		ModItems.initialize();
		ModItemGroups.initialize();
		ModDamageTypes.initialize();
		ServerNetworking.initialize();

		//TODO
		// - Плохое самочувствие при низком PlayerHp
		// - Не пишется, от чего игрок умер, и не только. Короче DamageSource bug
		// - BodyWidget улучшить
		// - Чтобы заходя в инвентарь можно было читать мудлы

		log.info(NAME + " initialized!");
	}

	public static ResourceLocation getResourceLocation(String path) {
		return new ResourceLocation(ID, path);
	}
}
