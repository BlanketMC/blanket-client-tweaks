package io.github.blanketmc.blanket;


import io.github.blanketmc.blanket.config.ConfigEntry;

import static io.github.blanketmc.blanket.config.ConfigEntry.Category.*;

public final class Config {

    //by KosmX
    @ConfigEntry(
            description = "The only internal config. Toggle how much log should blanket create",
            categories = EXPERIMENTAL
    )
    public static boolean extraLog = false;

    //by KosmX
    @ConfigEntry(
            description = "Fix inventory can not be closed by mouse button.",
            issues = "MC-577",
            categories = {BUGFIX, RECOMMENDED}
    )
    public static boolean mouseCloseNotHandledFix = true;

    //by FX - PR0CESS
    @ConfigEntry(
            description = "Fix chat lag on multiplayer servers",
            issues = "MC-247973",
            categories = {BUGFIX, RECOMMENDED}
    )
    public static boolean chatLagFix = true;

    //by FX - PR0CESS
    @ConfigEntry(
            description = "Fix lava damaging entities client-side leading to de-sync",
            issues = "MC-246465",
            categories = {BUGFIX, EXPERIMENTAL}
    )
    public static boolean lavaDamageDesyncFix = false;

    //by FX - PR0CESS
    @ConfigEntry(
            description = "Fix client damaging items & experience orbs leading to de-sync",
            issues = "MC-53850",
            categories = {BUGFIX, EXPERIMENTAL}
    )
    public static boolean entityDamageDesyncFix = false;

    //by FX - PR0CESS
    @ConfigEntry(
            description = "Fix guardian beam not rendering due to world time being too high",
            issues = "MC-165595",
            categories = {BUGFIX, RECOMMENDED}
    )
    public static boolean guardianBeamRenderFix = true;

    //by FX - PR0CESS
    @ConfigEntry(
            description = "Fix firework particles flashing when flying straight up",
            issues = "MC-245937",
            categories = {BUGFIX, RECOMMENDED}
    )
    public static boolean flashingFireworkParticlesFix = true;



    /*

    Entry Listeners

     */
}
