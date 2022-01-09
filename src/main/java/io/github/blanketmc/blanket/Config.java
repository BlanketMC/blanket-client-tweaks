package io.github.blanketmc.blanket;


import io.github.blanketmc.blanket.config.ConfigEntry;

import static io.github.blanketmc.blanket.config.ConfigEntry.Category.*;

public final class Config {

    @ConfigEntry(
            description = "The only internal config. Toggle how much log should blanket create",
            categories = {EXPERIMENTAL}
    )
    public static boolean extraLog = false;

    @ConfigEntry(
            description = "🛰️",
            categories = {RECOMMENDED}
    )
    public static boolean superSecretOption = false;

    @ConfigEntry(
            description = "asd",
            categories = {EXPERIMENTAL, TWEAK}
    )
    public static boolean anotherTestButton = false;

    @ConfigEntry(
            description = "select a category",
            categories = {EXPERIMENTAL, RECOMMENDED}
    )
    public static ConfigEntry.Category enumOption = RECOMMENDED;

    @ConfigEntry(
            description = "Fix inventory can not be closed by mouse button.",
            issues = "MC-577",
            categories = {BUGFIX, RECOMMENDED}
    )
    public static boolean mouseCloseNotHandledFix = true;

    @ConfigEntry(
            description = "Fix chat lag on multiplayer servers",
            categories = {BUGFIX, RECOMMENDED}
    )
    public static boolean chatLagFix = true;

    //not annotated to catch errors
    public static boolean theBlackSheep = true;
}
