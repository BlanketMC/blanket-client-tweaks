package io.github.blanketmc.blanket;

import io.github.blanketmc.blanket.config.ConfigHelper;
import net.fabricmc.api.ClientModInitializer;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.atomic.AtomicInteger;

public class ClientFixes implements ClientModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LogManager.getLogger("blanket-client-tweaks");

	public static final String mcIssuePrefix = "https://bugs.mojang.com/projects/MC/issues/";

	public static Config config;

	@Override
	public void onInitializeClient() {

		ConfigHelper.loadConfig();

		log(Level.INFO, "Loading Blanket, enabling " + countEnabledFeatures() + " feature(s).", true);

	}

	public static int countEnabledFeatures() {
		return ConfigHelper.countActiveConfigOptions(config);
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
