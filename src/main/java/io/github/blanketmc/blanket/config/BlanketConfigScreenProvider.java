package io.github.blanketmc.blanket.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import io.github.blanketmc.blanket.FabricModInitializer;

/**
 * Creates a cloth-config screen for mod configs
 */
public class BlanketConfigScreenProvider implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> ConfigHelper.getScreen(parent, FabricModInitializer.config);
    }
}
