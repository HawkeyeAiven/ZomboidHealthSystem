package aiven.zomboidhealthsystem;

import aiven.zomboidhealthsystem.foundation.utility.ItemUtil;
import aiven.zomboidhealthsystem.foundation.network.ClientPackets;
import aiven.zomboidhealthsystem.foundation.network.ServerTasks;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class ZomboidHealthSystem implements ModInitializer {
	@Override
	public void onInitialize() {

        LOGGER.info(ID_VER + " initializing...");

		try {
			Config.initialize();
		} catch (Exception e) {
			throw new RuntimeException("error in config");
		}

		ModItems.initialize();
		ModItemGroups.initialize();
		ClientPackets.initialize();
		ServerTasks.initialize();
		ModDamageTypes.initialize();
		ItemUtil.initialize();
    }

	public static final String ID = "zomboidhealthsystem";
	public static final String NAME = "Zomboid Health System";
	public static final String VERSION = "1.5";
	public static final String ID_VER = ID + "-" + VERSION;
	public static final int UPDATE_FREQUENCY = 5;
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);
	public static Random RANDOM = new Random();
}