package io.github.blanketmc.blanket;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FabricModInitializer implements ClientModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LogManager.getLogger("blanket-client-tweaks");

	public static final Config config = new Config();

	@Override
	public void onInitializeClient() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");

		//TODO load config, save if not loaded yet


	}

	public static void log(Level level, String message){
		log(level, message, false);
	}

	/**
	 * Log messages
	 * @param level   logging level
	 * @param message logged message
	 * @param noDebug should be printed even if {@link Config#extraLog} is disabled
	 */
	public static void log(Level level, String message, boolean noDebug){
		LOGGER.log(level, message);
	}

}
