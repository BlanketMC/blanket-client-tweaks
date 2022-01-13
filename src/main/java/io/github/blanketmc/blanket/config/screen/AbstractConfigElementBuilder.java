package io.github.blanketmc.blanket.config.screen;

import io.github.blanketmc.blanket.ClientFixes;
import io.github.blanketmc.blanket.config.ConfigHelper;
import io.github.blanketmc.blanket.config.EntryListener;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.*;
import net.minecraft.text.Text;
import org.apache.logging.log4j.Level;

import java.lang.reflect.Field;
@SuppressWarnings({"unchecked", "rawtypes"})
public abstract class AbstractConfigElementBuilder {
    protected final Field configField;

    public AbstractConfigElementBuilder(Field field) {
        this.configField = field;
    }

    protected abstract Text getTooltip();

    protected abstract Text getText();

    protected Class<? extends EntryListener>[] getListeners() {
        return new Class[] {};
    }

    public AbstractConfigListEntry<?> createConfigEntry() throws IllegalAccessException {
        ConfigEntryBuilder entryBuilder = ConfigEntryBuilder.create();
        Class<?> type = configField.getType();


        if (type.equals(Boolean.TYPE)) {
            BooleanToggleBuilder entry = entryBuilder.startBooleanToggle(getText(), configField.getBoolean(null));

            entry.setSaveConsumer(aBoolean -> { //saveConsumer
                try {
                    if (getListeners().length > 0) {
                        Boolean currentValue = configField.getBoolean(null);
                        for (Class<? extends EntryListener> listener : getListeners()) {
                            aBoolean = (Boolean) (ConfigHelper.callClassConstructor(listener)).onEntryChange(currentValue, aBoolean);
                        }
                    }
                    configField.set(null, aBoolean);
                } catch(IllegalAccessException e) {
                    e.printStackTrace();
                }
            });

            entry.setDefaultValue((boolean) ConfigHelper.getDefaultValue(configField)); //default value using mirror class
            entry.setTooltip(getTooltip());
            return entry.build();
        } else if (type.equals(Float.TYPE)) {
            FloatFieldBuilder entry = entryBuilder.startFloatField(getText(), configField.getFloat(null));

            entry.setSaveConsumer(aFloat -> { //saveConsumer
                try {
                    if (getListeners().length > 0) {
                        Float currentValue = configField.getFloat(null);
                        for (Class<? extends EntryListener> listener : getListeners()) {
                            aFloat = (Float) (ConfigHelper.callClassConstructor(listener)).onEntryChange(currentValue, aFloat);
                        }
                    }
                    configField.set(null, aFloat);
                } catch(IllegalAccessException e) {
                    e.printStackTrace();
                }
            });

            entry.setDefaultValue((float) ConfigHelper.getDefaultValue(configField)); //default value using mirror class
            entry.setTooltip(getTooltip());
            return entry.build();
        } else if (type.equals(Double.TYPE)) {
            DoubleFieldBuilder entry = entryBuilder.startDoubleField(getText(), configField.getDouble(null));

            entry.setSaveConsumer(aDouble -> { //saveConsumer
                try {
                    if (getListeners().length > 0) {
                        Double currentValue = configField.getDouble(null);
                        for (Class<? extends EntryListener> listener : getListeners()) {
                            aDouble = (Double) (ConfigHelper.callClassConstructor(listener)).onEntryChange(currentValue, aDouble);
                        }
                    }
                    configField.set(null, aDouble);
                } catch(IllegalAccessException e) {
                    e.printStackTrace();
                }
            });
            entry.setDefaultValue((double) ConfigHelper.getDefaultValue(configField));//default value using mirror class
            entry.setTooltip(getTooltip());
            return entry.build();
        } else if (type.equals(Integer.TYPE)) {
            IntFieldBuilder entry = entryBuilder.startIntField(getText(), configField.getInt(null));

            entry.setSaveConsumer(aInt -> { //saveConsumer
                try {
                    if (getListeners().length > 0) {
                        Integer currentValue = configField.getInt(null);
                        for (Class<? extends EntryListener> listener : getListeners()) {
                            aInt = (Integer) (ConfigHelper.callClassConstructor(listener)).onEntryChange(currentValue, aInt);
                        }
                    }
                    configField.set(null, aInt);
                } catch(IllegalAccessException e) {
                    e.printStackTrace();
                }
            });
            entry.setDefaultValue((int) ConfigHelper.getDefaultValue(configField));//default value using mirror class
            entry.setTooltip(getTooltip());
            return entry.build();
        } else if (type.equals(Long.TYPE)) {
            LongFieldBuilder entry = entryBuilder.startLongField(getText(), configField.getLong(null));

            entry.setSaveConsumer(aLong -> { //saveConsumer
                try {
                    if (getListeners().length > 0) {
                        Long currentValue = configField.getLong(null);
                        for (Class<? extends EntryListener> listener : getListeners()) {
                            aLong = (Long) (ConfigHelper.callClassConstructor(listener)).onEntryChange(currentValue, aLong);
                        }
                    }
                    configField.set(null, aLong);
                } catch(IllegalAccessException e) {
                    e.printStackTrace();
                }
            });
            entry.setDefaultValue((long) ConfigHelper.getDefaultValue(configField));//default value using mirror class
            entry.setTooltip(getTooltip());
            return entry.build();
        } else if (type.equals(String.class)) {
            TextFieldBuilder entry = entryBuilder.startTextField(getText(), (String) configField.get(null));

            entry.setSaveConsumer(aString -> { //saveConsumer
                try {
                    if (getListeners().length > 0) {
                        String currentValue = (String) configField.get(null);
                        for (Class<? extends EntryListener> listener : getListeners()) {
                            aString = (String) (ConfigHelper.callClassConstructor(listener)).onEntryChange(currentValue, aString);
                        }
                    }
                    configField.set(null, aString);
                } catch(IllegalAccessException e) {
                    e.printStackTrace();
                }
            });
            entry.setDefaultValue((String) ConfigHelper.getDefaultValue(configField));//default value using mirror class
            entry.setTooltip(getTooltip());
            return entry.build();
        } else if (type.isEnum()) {
            Class<Enum<?>> clazz = (Class<Enum<?>>) type;
            Object obj = configField.get(null);
            EnumSelectorBuilder<Enum<?>> entry = entryBuilder.startEnumSelector(getText(), clazz, clazz.cast(obj));

            entry.setSaveConsumer(anEnum -> {
                try {
                    if (getListeners().length > 0) {
                        Enum<?> currentValue = clazz.cast(configField.get(null));
                        for (Class<? extends EntryListener> listener : getListeners()) {
                            anEnum = (Enum<?>) (ConfigHelper.callClassConstructor(listener)).onEntryChange(currentValue, anEnum);
                        }
                    }
                    configField.set(null, anEnum);
                } catch(IllegalAccessException e) {
                    e.printStackTrace();
                }
            });

            //default value using mirror class
            Object defVal = ConfigHelper.getDefaultValue(configField);
            entry.setDefaultValue(clazz.cast(defVal));

            //Description
            entry.setTooltip(getTooltip());

            return entry.build();

        } else {
            ClientFixes.log(Level.ERROR, "Config: " + configField.getName() + " can not be displayed: Unknown type", true);
            throw new UnsupportedOperationException();
        }
    }
}
