package io.github.blanketmc.blanket;


import io.github.blanketmc.blanket.config.ConfigEntry;

import static io.github.blanketmc.blanket.config.ConfigEntry.Category.*;

public final class Config {
    public static Config config = new Config();

    @ConfigEntry(
            description = "The only internal config. Toggle how much log should blanket create", categories = {EXPERIMENTAL})
    public boolean extraLog = false;

    @ConfigEntry(description = "🛰️", categories = {RECOMMENDED})
    public boolean superSecretOption = false;

    @ConfigEntry(description = "asd", categories = {EXPERIMENTAL, TWEAK})
    public boolean anotherTestButton = false;

    @ConfigEntry(description = "select a category", categories = {EXPERIMENTAL, RECOMMENDED})
    public ConfigEntry.Category enumOption = RECOMMENDED;

    //not annotated to catch errors
    public boolean theBlackSheep = true;
}
