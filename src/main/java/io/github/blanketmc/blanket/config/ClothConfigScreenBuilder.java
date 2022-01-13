package io.github.blanketmc.blanket.config;

import io.github.blanketmc.blanket.Config;
import io.github.blanketmc.blanket.ClientFixes;
import io.github.blanketmc.blanket.config.screen.ScreenHelper;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.DropdownBoxEntry;
import me.shedaniel.clothconfig2.gui.entries.EnumListEntry;
import me.shedaniel.clothconfig2.impl.builders.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import org.apache.logging.log4j.Level;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * Creates a cloth-config screen for mod configs
 *
 * USE THIS CLASS ONLY FOR MOD SCREEN
 * Or the mod will crash if no ModMenu / Cloth Config is installed.
 */
@SuppressWarnings({"unchecked", "rawtypes", "unused"})
@Deprecated
public class ClothConfigScreenBuilder {

    public static Screen getScreen(Screen parent, Config config) {
        ConfigBuilder builder = ConfigBuilder.create().setParentScreen(parent).setTitle(new TranslatableText("blanket-client-tweaks.config.title"));
        builder.setSavingRunnable(ConfigHelper::saveConfig);

        //Config entry category
        ConfigCategory general = builder.getOrCreateCategory(new TranslatableText("blanket-client-tweaks.config.general")); //we can ignore the title, until we have more categories
        addEntriesToCategory(general, builder.entryBuilder(), config);

        ConfigCategory bulkActions = builder.getOrCreateCategory(new TranslatableText("blanket-client-tweaks.config.bulk"));
        addBulkModeCategory(bulkActions, builder.entryBuilder(), config, parent);
        return builder.build();
    }

    private static void addEntriesToCategory(ConfigCategory category, ConfigEntryBuilder entryBuilder, Config config) {
        ConfigHelper.iterateOnConfig((field, configEntry) -> {
            Class<?> type = field.getType();
            if (type.equals(Boolean.TYPE)) {
                BooleanToggleBuilder entry = entryBuilder.startBooleanToggle(ConfigHelper.getTextComponent(configEntry.displayName(), field.getName()), field.getBoolean(config));

                entry.setSaveConsumer(aBoolean -> { //saveConsumer
                    try {
                        if (configEntry.listeners().length > 0) {
                            Boolean currentValue = field.getBoolean(config);
                            for (Class<? extends EntryListener> listener : configEntry.listeners()) {
                                aBoolean = (Boolean) (ConfigHelper.callClassConstructor(listener)).onEntryChange(currentValue, aBoolean);
                            }
                        }
                        field.set(config, aBoolean);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                });

                entry.setDefaultValue((boolean) ConfigHelper.getDefaultValue(field)); //default value using mirror class
                entry.setTooltip(fancyDescription(configEntry.description(), configEntry.categories(), configEntry.issues()));
                category.addEntry(entry.build());
            } else if (type.equals(Float.TYPE)) {
                FloatFieldBuilder entry = entryBuilder.startFloatField(ConfigHelper.getTextComponent(configEntry.displayName(), field.getName()), field.getFloat(config));

                entry.setSaveConsumer(aFloat -> { //saveConsumer
                    try {
                        if (configEntry.listeners().length > 0) {
                            Float currentValue = field.getFloat(config);
                            for (Class<? extends EntryListener> listener : configEntry.listeners()) {
                                aFloat = (Float)(ConfigHelper.callClassConstructor(listener)).onEntryChange(currentValue, aFloat);
                            }
                        }
                        field.set(config, aFloat);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                });

                entry.setDefaultValue((float) ConfigHelper.getDefaultValue(field)); //default value using mirror class
                entry.setTooltip(fancyDescription(configEntry.description(), configEntry.categories(), configEntry.issues()));
                category.addEntry(entry.build());
            } else if (type.equals(Double.TYPE)) {
                DoubleFieldBuilder entry = entryBuilder.startDoubleField(ConfigHelper.getTextComponent(configEntry.displayName(), field.getName()), field.getDouble(config));

                entry.setSaveConsumer(aDouble -> { //saveConsumer
                    try {
                        if (configEntry.listeners().length > 0) {
                            Double currentValue = field.getDouble(config);
                            for (Class<? extends EntryListener> listener : configEntry.listeners()) {
                                aDouble = (Double)(ConfigHelper.callClassConstructor(listener)).onEntryChange(currentValue, aDouble);
                            }
                        }
                        field.set(config, aDouble);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                });
                entry.setDefaultValue((double) ConfigHelper.getDefaultValue(field));//default value using mirror class
                entry.setTooltip(fancyDescription(configEntry.description(), configEntry.categories(), configEntry.issues()));
                category.addEntry(entry.build());
            } else if (type.equals(Integer.TYPE)) {
                IntFieldBuilder entry = entryBuilder.startIntField(ConfigHelper.getTextComponent(configEntry.displayName(), field.getName()), field.getInt(config));

                entry.setSaveConsumer(aInt -> { //saveConsumer
                    try {
                        if (configEntry.listeners().length > 0) {
                            Integer currentValue = field.getInt(config);
                            for (Class<? extends EntryListener> listener : configEntry.listeners()) {
                                aInt = (Integer)(ConfigHelper.callClassConstructor(listener)).onEntryChange(currentValue, aInt);
                            }
                        }
                        field.set(config, aInt);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                });
                entry.setDefaultValue((int) ConfigHelper.getDefaultValue(field));//default value using mirror class
                entry.setTooltip(fancyDescription(configEntry.description(), configEntry.categories(), configEntry.issues()));
                category.addEntry(entry.build());
            } else if (type.equals(Long.TYPE)) {
                LongFieldBuilder entry = entryBuilder.startLongField(ConfigHelper.getTextComponent(configEntry.displayName(), field.getName()), field.getLong(config));

                entry.setSaveConsumer(aLong -> { //saveConsumer
                    try {
                        if (configEntry.listeners().length > 0) {
                            Long currentValue = field.getLong(config);
                            for (Class<? extends EntryListener> listener : configEntry.listeners()) {
                                aLong = (Long)(ConfigHelper.callClassConstructor(listener)).onEntryChange(currentValue, aLong);
                            }
                        }
                        field.set(config, aLong);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                });
                entry.setDefaultValue((long) ConfigHelper.getDefaultValue(field));//default value using mirror class
                entry.setTooltip(fancyDescription(configEntry.description(), configEntry.categories(), configEntry.issues()));
                category.addEntry(entry.build());
            } else if (type.equals(String.class)) {
                TextFieldBuilder entry = entryBuilder.startTextField(ConfigHelper.getTextComponent(configEntry.displayName(), field.getName()), (String)field.get(config));

                entry.setSaveConsumer(aString -> { //saveConsumer
                    try {
                        if (configEntry.listeners().length > 0) {
                            String currentValue = (String)field.get(config);
                            for (Class<? extends EntryListener> listener : configEntry.listeners()) {
                                aString = (String)(ConfigHelper.callClassConstructor(listener)).onEntryChange(currentValue, aString);
                            }
                        }
                        field.set(config, aString);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                });
                entry.setDefaultValue((String) ConfigHelper.getDefaultValue(field));//default value using mirror class
                entry.setTooltip(fancyDescription(configEntry.description(), configEntry.categories(), configEntry.issues()));
                category.addEntry(entry.build());
            } else if (type.isEnum()) {
                Class<Enum<?>> clazz = (Class<Enum<?>>) type;
                Object obj = field.get(config);
                EnumSelectorBuilder<Enum<?>> entry = entryBuilder.startEnumSelector(ConfigHelper.getTextComponent(configEntry.displayName(), field.getName()), clazz, clazz.cast(obj));

                entry.setSaveConsumer(anEnum -> {
                    try {
                        if (configEntry.listeners().length > 0) {
                            Enum<?> currentValue = clazz.cast(field.get(config));
                            for (Class<? extends EntryListener> listener : configEntry.listeners()) {
                                anEnum = (Enum<?>)(ConfigHelper.callClassConstructor(listener)).onEntryChange(currentValue, anEnum);
                            }
                        }
                        field.set(config, anEnum);
                    } catch(IllegalAccessException e) {
                        e.printStackTrace();
                    }
                });

                //default value using mirror class
                Object defVal = ConfigHelper.getDefaultValue(field);
                entry.setDefaultValue(clazz.cast(defVal));

                //Description
                entry.setTooltip(fancyDescription(configEntry.description(), configEntry.categories(), configEntry.issues()));

                category.addEntry(entry.build());

            } else {
                ClientFixes.log(Level.ERROR, "Config: " + field.getName() + " can not be displayed: Unknown type", true);
            }
        });
    }

    private static Text fancyDescription(String desc, ConfigEntry.Category[] categories, String[] issues) {
        return ScreenHelper.fancyDescription(desc, categories, issues);
    }

    private static void addBulkModeCategory(ConfigCategory category, ConfigEntryBuilder entryBuilder, Config config, Screen parent) {
        ActionData action = new ActionData();

        // Action selector button
        EnumSelectorBuilder<ActionType> actionEntry = entryBuilder.startEnumSelector(
                new TranslatableText("blanket-client-tweaks.config.chooseBulk"),
                ActionType.class,
                action.action);

        actionEntry.setSaveConsumer(actionType -> action.action = actionType);
        actionEntry.setDefaultValue(ActionType.ENABLE);

        EnumListEntry<ActionType> actionSelectorButton = actionEntry.build();
        category.addEntry(actionSelectorButton);


        //Category selector button
        DropdownMenuBuilder<ConfigEntry.Category> typeSelector = entryBuilder.startDropdownMenu(
                new TranslatableText("blanket-client-tweaks.config.chooseCategory"),
                DropdownMenuBuilder.TopCellElementBuilder.of(action.category, s -> {
                    try {
                        return ConfigEntry.Category.valueOf(s);
                    } catch(IllegalArgumentException ignore) { }
                    return null;
                }, anEnum -> new LiteralText(anEnum.toString())),
                DropdownMenuBuilder.CellCreatorBuilder.of(category1 -> new LiteralText(category1.toString())));

        typeSelector.setDefaultValue(ConfigEntry.Category.ALL);
        typeSelector.setSelections(Arrays.stream(ConfigEntry.Category.values()).collect(Collectors.toSet()));


        MutableText categoryTypes = new LiteralText("Possible categories:").formatted(Formatting.GOLD);
        for (ConfigEntry.Category categoryEnum : ConfigEntry.Category.values()) {
            categoryTypes.append(new LiteralText("\n" + categoryEnum.toString()).formatted(Formatting.BLUE));
        }
        typeSelector.setTooltip(categoryTypes);


        typeSelector.setSaveConsumer(anEnum -> action.category = anEnum);
        DropdownBoxEntry<ConfigEntry.Category> typeSelectorButton = typeSelector.build();

        category.addEntry(typeSelectorButton);


        // Pressable action button
        PressableButtonEntry actionButton = new PressableButtonEntry(new TranslatableText("blanket-client-tweaks.config.doBulkAction"), () -> {
            if (typeSelectorButton.getError().isPresent()) return;
            action.action = actionSelectorButton.getValue();
            action.category = typeSelectorButton.getValue();

            MinecraftClient.getInstance().setScreen(new ConfirmScreen(t -> {
                if (t) {
                    ConfigHelper.iterateOnConfig((field, configEntry) -> {
                        if (field.getType().equals(Boolean.TYPE) && (action.category.equals(ConfigEntry.Category.ALL) || Arrays.stream(field.getAnnotation(ConfigEntry.class).categories()).anyMatch(category12 -> category12 == action.category))) {
                            field.set(null, action.action.apply(field.getBoolean(config)));
                        }
                    });
                    ConfigHelper.saveConfig();
                }
                MinecraftClient.getInstance().setScreen(getScreen(parent, config));
            }, new TranslatableText("blanket-client-tweaks.config.confirmTitle"), new TranslatableText(
                    "blanket-client-tweaks.config.confirmText",
                    new LiteralText(action.action.toString()).formatted(Formatting.GREEN),
                    new LiteralText(action.category.toString()).formatted(Formatting.BLUE)
            )));
        },
                () -> {

                    Text actionText = new LiteralText(actionSelectorButton.getValue().toString()).formatted(Formatting.GREEN);
                    if (typeSelectorButton.getError().isEmpty()) {
                        action.category = typeSelectorButton.getValue();
                    }

                    Text category13 = new LiteralText(action.category.toString()).formatted(Formatting.BLUE);


                    return new TranslatableText("blanket-client-tweaks.config.doBulkAction", actionText, category13);
                });

        //3 nested lambdas :D


        category.addEntry(entryBuilder.startTextDescription(new TranslatableText("blanket-client-tweaks.config.bulkDescription")).build());
        category.addEntry(actionButton);


    }

    private enum ActionType {
        ENABLE(b -> true),
        DISABLE(b -> false),
        TOGGLE(b -> !b),
        ;
        private final Function<Boolean, Boolean> applier;

        ActionType(Function<Boolean, Boolean> applier) {
            this.applier = applier;
        }

        public boolean apply(boolean b) {
            return applier.apply(b);
        }
    }

    private static class ActionData {
        ActionType action = ActionType.ENABLE;
        ConfigEntry.Category category = ConfigEntry.Category.ALL;
    }
}
