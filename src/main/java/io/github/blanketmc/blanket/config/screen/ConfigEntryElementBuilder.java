package io.github.blanketmc.blanket.config.screen;

import io.github.blanketmc.blanket.config.ConfigEntry;
import io.github.blanketmc.blanket.config.ConfigHelper;
import io.github.blanketmc.blanket.config.EntryListener;
import net.minecraft.text.Text;

import java.lang.reflect.Field;

@SuppressWarnings("rawtypes")
public class ConfigEntryElementBuilder extends AbstractConfigElementBuilder {
    private final ConfigEntry configEntry;

    public ConfigEntryElementBuilder(Field field) throws IllegalArgumentException {
        super(field);
        if (!field.isAnnotationPresent(ConfigEntry.class)) throw new IllegalArgumentException(field + " is not a config entry");
        configEntry = field.getAnnotation(ConfigEntry.class);
    }

    @Override
    protected Text getTooltip() {
        return ScreenHelper.fancyDescription(configEntry.description(), configEntry.categories(), configEntry.issues());
    }

    @Override
    protected Text getText() {
        return ConfigHelper.getTextComponent(configEntry.displayName(), configField.getName());
    }

    @Override
    protected Class<? extends EntryListener>[] getListeners() {
        return configEntry.listeners();
    }
}
