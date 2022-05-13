package io.github.blanketmc.blanket.config.screen;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

/**
 * Do not use this class for anything except for ModMenu API entrypoint
 *
 * Loading this class without ModMenu will crash the game.
 */
public class BlanketConfigScreenProvider implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return BlanketConfigScreen::new;
    }
}
