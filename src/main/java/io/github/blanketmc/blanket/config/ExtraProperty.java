package io.github.blanketmc.blanket.config;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ExtraProperty {

    /**
     * @return extraPropertyName
     */
    String name() default "";

    /**
     * @return The tooltip of the extra property
     */
    String description() default "";
}
