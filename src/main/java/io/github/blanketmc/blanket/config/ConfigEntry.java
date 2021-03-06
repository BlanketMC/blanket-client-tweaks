package io.github.blanketmc.blanket.config;

import java.lang.annotation.*;

/**
 * The config annotation
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ConfigEntry {

    /**
     * The name of the config entry
     * @return name, empty if we use the variable name
     */
    String name() default "";

    /**
     * The display name of the entry.
     * If starts with `blanket-client-tweaks.` then a translatable entry
     * @return display name
     */
    String displayName() default "";

    /**
     * The description of the config entry.
     * If starts with `blanket-client-tweaks.` then a translatable entry
     * @return config description
     */
    String description() default "";

    /**
     * The MC issue it fixes. leave empty, if no issue
     * Only the MC-??? part, never the https:
     * @return MC-????
     */
    String[] issues() default {};


    /**
     * Allows you to add a listener to the config entry.
     * @return Class<? extends ConfigEntryListener>
     */
    Class<? extends EntryListener>[] listeners() default {};

    /**
     * @return categories
     */
    Category[] categories();

    /**
     * @return Extra properties for config entry
     */
    String[] extraProperties() default {};

    /**
     * Enum to describe config entry types
     */
    enum Category {

        /**
         * Recommended config, defaults to ON.
         */
        RECOMMENDED("RECOMMENDED"),

        /**
         * Fixes a bug in Minecraft.
         */
        BUGFIX("BUGFIX"),

        /**
         * A Client-tweak, not a bugfix. Still can be very useful.
         */
        TWEAK("TWEAK"),

        /**
         * Experimental solution, be careful with these!
         */
        EXPERIMENTAL("EXPERIMENTAL"),
  
        /**
         * This means that the fix/tweak only works in singleplayer
         */
        SINGLEPLAYER("SINGLEPLAYER"),

        /**
         * The fix/tweak is related to Rendering.
         */
        RENDER("RENDER"),

        /**
         * The fix/tweak is related to User Interface.
         */
        UI("UI"),

        /**
         * The fix/tweak can be considered as a performance fix.
         */
        PERFORMANCE("PERFORMANCE"),

        /**
         * All config, just a tool for bulk-actions.
         */
        ALL("ALL"),
        ;
        final String key;

        Category(String string) {
            this.key = string;
        }
    }
}
