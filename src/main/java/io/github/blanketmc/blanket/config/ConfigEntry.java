package io.github.blanketmc.blanket.config;

import java.lang.annotation.*;

/**
 * The config annotation
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
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
     * @return categories
     */
    Category[] categories();

    /**
     * Enum to describe config entry types
     */
    public static enum Category {
        RECOMMENDED("RECOMMENDED"),
        BUGFIX("BUGFIX"),
        TWEAK("TWEAK"),
        EXPERIMENTAL("EXPERIMENTAL"),
        ;
        final String key;

        Category(String string) {
            this.key = string;
        }
        /*
        public String getKey() {
            return key;
        }

         */
    }
}
