package io.github.blanketmc.blanket.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import io.github.blanketmc.blanket.Config;
import io.github.blanketmc.blanket.FabricClientModInitializer;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.DropdownMenuBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import org.apache.logging.log4j.Level;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * Creates a cloth-config screen for mod configs
 */
@SuppressWarnings("unchecked")
public class BlanketConfigScreenProvider implements ModMenuApi {
    private static final Config defaults = new Config();


    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> getScreen(parent, Config.config);
    }

    public static Screen getScreen(Screen parent, Config config) {
        ConfigBuilder builder = ConfigBuilder.create().setParentScreen(parent).setTitle(new TranslatableText("blanket-client-tweaks.config.title"));
        builder.setSavingRunnable(() -> ConfigHelper.saveConfig(config));

        //Config entry category
        ConfigCategory general = builder.getOrCreateCategory(new TranslatableText("blanket-client-tweaks.config.general")); //we can ignore the title, until we have more categories
        addEntriesToCategory(general, builder.entryBuilder(), config);

        ConfigCategory bulkActions = builder.getOrCreateCategory(new TranslatableText("blanket-client-tweaks.config.bulk"));
        addBulkModeCategory(bulkActions, builder.entryBuilder(), config, parent);
        return builder.build();
    }

    private static void addEntriesToCategory(ConfigCategory category, ConfigEntryBuilder entryBuilder, Config config) {
        ConfigHelper.iterateOnConfig((field, configEntry) -> {
            var type = field.getType();
            if (type.equals(Boolean.TYPE)) {

                var entry = entryBuilder.startBooleanToggle(ConfigHelper.getTextComponent(configEntry.displayName(), field.getName()), field.getBoolean(config));

                //saveConsumer
                entry.setSaveConsumer(aBoolean -> {
                    try {
                        field.set(config, aBoolean);
                    } catch(IllegalAccessException e) {
                        e.printStackTrace();
                    }
                });

                //default value using mirror class
                boolean defVal = field.getBoolean(defaults);
                entry.setDefaultValue(defVal);

                //Description
                entry.setTooltip(fancyDescription(configEntry.description(), configEntry.categories(), configEntry.issue()));

                category.addEntry(entry.build());

            } else if (type.isEnum()) {

                var clazz = (Class<Enum<?>>) type;

                Object obj = field.get(config);
                var entry = entryBuilder.startEnumSelector(ConfigHelper.getTextComponent(configEntry.displayName(), field.getName()), clazz, clazz.cast(obj));

                entry.setSaveConsumer(anEnum -> {
                    try {
                        field.set(config, anEnum);
                    } catch(IllegalAccessException e) {
                        e.printStackTrace();
                    }
                });

                //default value using mirror class
                Object defVal = field.get(defaults);
                entry.setDefaultValue(clazz.cast(defVal));

                //Description
                entry.setTooltip(fancyDescription(configEntry.description(), configEntry.categories(), configEntry.issue()));

                category.addEntry(entry.build());

            } else {
                FabricClientModInitializer.log(Level.ERROR, "Config: " + field.getName() + " can not be displayed: Unknown type", true);
            }
        });
    }

    private static Text fancyDescription(String desc, ConfigEntry.Category[] categories, String issue) {
        MutableText description = new LiteralText("");
        if (!desc.equals("")) {
            description = ConfigHelper.getTextComponent(desc, null);

            description = description.formatted(Formatting.YELLOW).append(new LiteralText("\n"));

            if (issue.equals("")) description.append(new LiteralText("\n"));
        }

        if (!issue.equals("")) {
            description.append(new LiteralText("Fixes: ").formatted(Formatting.DARK_PURPLE).append(new LiteralText(issue).formatted(Formatting.DARK_AQUA))).append("\n\n");
        }

        description.append(new LiteralText("Categories:\n").formatted(Formatting.LIGHT_PURPLE));

        var iterator = Arrays.stream(categories).iterator();
        while (iterator.hasNext()) {

            var category = iterator.next();
            description.append(new LiteralText(category.toString()).formatted(Formatting.BLUE));

            if (iterator.hasNext()) {
                description.append(new LiteralText(" + ").formatted(Formatting.GOLD));
            }
        }
        return description;
    }

    public static void addBulkModeCategory(ConfigCategory category, ConfigEntryBuilder entryBuilder, Config config, Screen parent) {
        var action = new ActionData();

        // Action selector button
        var actionEntry = entryBuilder.startEnumSelector(
                new TranslatableText("blanket-client-tweaks.config.chooseBulk"),
                ActionType.class,
                action.action);

        actionEntry.setSaveConsumer(actionType -> action.action = actionType);
        actionEntry.setDefaultValue(ActionType.ENABLE);

        var actionSelectorButton = actionEntry.build();
        category.addEntry(actionSelectorButton);


        //Category selector button
        var typeSelector = entryBuilder.startDropdownMenu(
                new TranslatableText("blanket-client-tweaks.config.chooseCategory"),
                DropdownMenuBuilder.TopCellElementBuilder.of(action.category, s -> {
                    try {
                        return ConfigEntry.Category.valueOf(s);
                    } catch(IllegalArgumentException ignore) { }
                    return null;
                }, anEnum -> new LiteralText(anEnum.toString())),
                DropdownMenuBuilder.CellCreatorBuilder.of(category1 -> new LiteralText(category1.toString())));

        typeSelector.setDefaultValue(ConfigEntry.Category.RECOMMENDED);
        typeSelector.setSelections(Arrays.stream(ConfigEntry.Category.values()).collect(Collectors.toSet()));


        MutableText categoryTypes = new LiteralText("Possible categories:").formatted(Formatting.GOLD);
        for (var categoryEnum : ConfigEntry.Category.values()) {
            categoryTypes.append(new LiteralText("\n" + categoryEnum.toString()).formatted(Formatting.BLUE));
        }
        typeSelector.setTooltip(categoryTypes);


        typeSelector.setSaveConsumer(anEnum -> action.category = (ConfigEntry.Category) anEnum);
        var typeSelectorButton = typeSelector.build();

        category.addEntry(typeSelectorButton);


        // Pressable action button
        var actionButton = new PressableButtonEntry(new TranslatableText("blanket-client-tweaks.config.doBulkAction"), () -> {
            if (typeSelectorButton.getError().isPresent()) return;
            action.action = actionSelectorButton.getValue();
            action.category = typeSelectorButton.getValue();

            MinecraftClient.getInstance().setScreen(new ConfirmScreen(t -> {
                if (t) {
                    ConfigHelper.iterateOnConfig((field, configEntry) -> {
                        if (field.getType().equals(Boolean.TYPE) && Arrays.stream(field.getAnnotation(ConfigEntry.class).categories()).anyMatch(category12 -> category12 == action.category)) {
                            field.set(config, action.action.apply(field.getBoolean(config)));
                        }
                    });
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

    private static enum ActionType {
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
        ConfigEntry.Category category = ConfigEntry.Category.RECOMMENDED;
    }
}
