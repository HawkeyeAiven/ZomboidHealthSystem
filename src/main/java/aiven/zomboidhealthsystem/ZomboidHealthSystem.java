package aiven.zomboidhealthsystem;

import aiven.zomboidhealthsystem.json5.Json5;
import aiven.zomboidhealthsystem.network.ServerNetworking;

import net.fabricmc.api.ModInitializer;

import net.minecraft.resources.ResourceLocation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ZomboidHealthSystem implements ModInitializer {
	public static final String ID = "zomboidhealthsystem";
	public static final String NAME = "Zomboid Health System";
	public static final String VERSION = "2.0.2";
	public static final String ID_VER = ID + "-" + VERSION;

	public static final Logger log = LoggerFactory.getLogger(NAME);

	@Override
	public void onInitialize() {
		Json5.initialize();
		Config.initialize();

		ModDamageTypes.initialize();
		ModItems.initialize();
		ModItemGroups.initialize();
		ServerNetworking.initialize();

		log.info(NAME + " initialized!");

		//TODO идеи
		// - Показывать время только когда часы есть в инвентаре

		//TODO
		// - Время в разных форматах, менять его по нажатию как-нибудь
		// - Кровоточ части тела быстрее мигают когда на экране 2 виджета
		// - Чтобы во вкладке Health не рисовался бодивиджетхуд
	}

	public static ResourceLocation getResourceLocation(String path) {
		return new ResourceLocation(ID, path);
	}
}
