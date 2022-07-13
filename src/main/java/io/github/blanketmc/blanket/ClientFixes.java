package io.github.blanketmc.blanket;

import com.mojang.brigadier.CommandDispatcher;
import io.github.blanketmc.blanket.config.ConfigHelper;
import io.github.blanketmc.blanket.utils.ClientCommands;
import io.github.blanketmc.blanket.utils.KeyBindings;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

		KeyBindings.init();
		ClientCommandRegistrationCallback.EVENT.register(new ClientCommandRegistrationCallback() {
			@Override
			public void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
				ClientCommands.registerCommands(dispatcher);
			}
		});
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
