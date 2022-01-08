package io.github.blanketmc.blanket;

import io.github.blanketmc.blanket.config.ConfigHelper;
import net.fabricmc.api.ClientModInitializer;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.atomic.AtomicInteger;

public class FabricClientModInitializer implements ClientModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LogManager.getLogger("blanket-client-tweaks");

	@Override
	public void onInitializeClient() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.


		Config.config = ConfigHelper.loadConfig();

		log(Level.INFO, "Loading Blanket, enabling " + countEnabledFeatures() + " feature(s).", true);

	}

	public static int countEnabledFeatures() {
		AtomicInteger counter = new AtomicInteger(0);

		ConfigHelper.iterateOnConfig((field, configEntry) -> {
			if (field.getType().equals(Boolean.TYPE) && field.getBoolean(Config.config)) {
				counter.incrementAndGet();
			}
		});

		return counter.get();
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
