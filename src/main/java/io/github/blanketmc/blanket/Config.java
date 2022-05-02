package io.github.blanketmc.blanket;


import io.github.blanketmc.blanket.config.ConfigEntry;

import static io.github.blanketmc.blanket.config.ConfigEntry.Category.*;

public final class Config {

    //by KosmX
    @ConfigEntry(
            description = "The only internal config. Toggle how much log should blanket create",
            categories = EXPERIMENTAL
    )
    public static boolean extraLog = true;

    //by KosmX
    @ConfigEntry(
            description = "Fix inventory can not be closed by mouse button.",
            issues = "MC-577",
            categories = {BUGFIX, RECOMMENDED, UI}
    )
    public static boolean mouseCloseNotHandledFix = true;

    //by FX - PR0CESS
    @ConfigEntry(
            description = "Fix chat lag on multiplayer servers",
            issues = "MC-247973",
            categories = {BUGFIX, RECOMMENDED, PERFORMANCE}
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
            description = "Fix guardian beam not rendering due to world time being too high\nSodium-extra has the same fix",
            issues = "MC-165595",
            categories = {BUGFIX, RECOMMENDED, RENDER}
    )
    public static boolean guardianBeamRenderFix = true;

    //by FX - PR0CESS
    @ConfigEntry(
            description = "Fix firework particles flashing when flying straight up",
            issues = "MC-245937",
            categories = {BUGFIX, RECOMMENDED, RENDER}
    )
    public static boolean flashingFireworkParticlesFix = true;

    //by FX - PR0CESS
    @ConfigEntry(
            description = "Fix entity id being shared by the client renderer and integrated server",
            issues = "MC-238384",
            categories = {BUGFIX, SINGLEPLAYER}
    )
    public static boolean sharedEntityIdFix = true;

    //by FX - PR0CESS
    @ConfigEntry(
            description = "Optimized the getBiome call to be 25% - 75% faster",
            categories = {PERFORMANCE, RECOMMENDED}
    )
    public static boolean optimizedBiomeAccess = true;

    //by FX - PR0CESS
    @ConfigEntry(
            description = "Fix end crystals attempting to heal the ender dragon",
            issues = "MC-187100",
            categories = BUGFIX
    )
    public static boolean crystalsTargetDeadDragonFix = true;

    //by FX - PR0CESS
    @ConfigEntry(
            description = "Fix wolf tail spinning if health is increased",
            issues = "MC-175622",
            categories = {BUGFIX, RENDER}
    )
    public static boolean wolfTailSpinFix = true;

    //by KosmX
    @ConfigEntry(
            description = "Fix switched amethyst sound",
            issues = "MC-248223",
            categories = {BUGFIX, RECOMMENDED}
    )
    public static boolean fixSwappedAmethystSound = true;

    //by FX - PR0CESS
    @ConfigEntry(
            description = "Fixes Custom attribute modifiers that use the vanilla UUIDs don't display in tooltip properly",
            issues = "MC-251252",
            categories = BUGFIX
    )
    public static boolean itemStackTooltipUUIDFix = true;

    /*

    Entry Listeners

     */
}
