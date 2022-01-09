package io.github.blanketmc.blanket.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import io.github.blanketmc.blanket.ClientFixes;

public class BlanketConfigScreenProvider implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> ClothConfigScreenBuilder.getScreen(parent, ClientFixes.config);
    }
}
