package io.github.blanketmc.blanket.config.screen.util;

import io.github.blanketmc.blanket.config.ConfigHelper;
import io.github.blanketmc.blanket.config.ExtraProperty;
import net.minecraft.text.Text;

import java.lang.reflect.Field;

public class ExtraPropertyElementBuilder extends AbstractConfigElementBuilder {
    private final ExtraProperty extraProperty;

    public ExtraPropertyElementBuilder(Field field) {
        super(field);
        if (!field.isAnnotationPresent(ExtraProperty.class)) throw new IllegalArgumentException(field + " is not an extra property entry");
        this.extraProperty = field.getAnnotation(ExtraProperty.class);
    }

    @Override
    protected Text getTooltip() {
        return ConfigHelper.getTextComponent(extraProperty.description(), "");
    }

    @Override
    protected Text getText() {
        String[] fieldName = configField.getName().split("_");
        return ConfigHelper.getTextComponent(extraProperty.name(), fieldName[fieldName.length - 1]);
    }
}
