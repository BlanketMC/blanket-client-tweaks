package io.github.blanketmc.blanket.config.screen;

import io.github.blanketmc.blanket.config.ConfigEntry;
import io.github.blanketmc.blanket.config.screen.widget.BlanketConfigEntryList;
import io.github.blanketmc.blanket.config.screen.widget.PressableButtonEntry;
import me.shedaniel.clothconfig2.api.AbstractConfigEntry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.AbstractConfigScreen;
import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry;
import me.shedaniel.clothconfig2.gui.entries.DropdownBoxEntry;
import me.shedaniel.clothconfig2.gui.entries.EnumListEntry;
import me.shedaniel.clothconfig2.impl.builders.DropdownMenuBuilder;
import me.shedaniel.clothconfig2.impl.builders.EnumSelectorBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;


@SuppressWarnings({"unused", "rawtypes", "unchecked"})
public class BulkActionScreen extends AbstractConfigScreen {

    private BlanketConfigEntryList entryList;
    private final List<AbstractConfigEntry> entriesList;
    private List<Drawable> drawables = new ArrayList<>();
    private ButtonWidget quitButton;

    protected BulkActionScreen(BlanketConfigScreen parent) {
        super(parent, Text.translatable("blanket-client-tweaks.config.bulk"), DrawableHelper.OPTIONS_BACKGROUND_TEXTURE);
        List<AbstractConfigEntry> entries = new ArrayList<>();
        addBulkModeCategory(entries, ConfigEntryBuilder.create(), parent);
        entriesList = entries;
    }

    @Override
    protected void init() {
        drawables = new ArrayList<>();
        entryList = new BlanketConfigEntryList(this, client, this.width, this.height - 60, 30,this.height - 30);

        entryList.setElements(entriesList);
        this.addSelectableChild(entryList);

        int buttonWidths = Math.min(200, (this.width - 50 - 12) / 3);
        this.addDrawableChild(this.quitButton = new ButtonWidget((this.width + buttonWidths) / 2 - buttonWidths, this.height - 26, buttonWidths, 20, Text.translatable("gui.cancel"), (widget) -> this.quit()));

        super.init();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);

        entryList.render(matrices, mouseX, mouseY, delta);
        for (Drawable drawable : drawables) {
            drawable.render(matrices, mouseX, mouseY, delta);
        }


        drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 10, 16777215);

        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public Map<Text, List<AbstractConfigEntry<?>>> getCategorizedEntries() {
        Map<Text, List<AbstractConfigEntry<?>>> map = new HashMap<>();
        List<AbstractConfigEntry<?>> list = this.entriesList.stream().collect(
                (Supplier<List<AbstractConfigEntry<?>>>) ArrayList::new,
                List::add,
                List::addAll
        );
        map.put(Text.literal("bulk"), list);
        return map;
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


    private void addBulkModeCategory(List<AbstractConfigEntry> category, ConfigEntryBuilder entryBuilder, BlanketConfigScreen parent) {
        ActionData action = new ActionData();

        // Action selector button
        EnumSelectorBuilder<ActionType> actionEntry = entryBuilder.startEnumSelector(
                Text.translatable("blanket-client-tweaks.config.chooseBulk"),
                ActionType.class,
                action.action);

        actionEntry.setSaveConsumer(actionType -> action.action = actionType);
        actionEntry.setDefaultValue(ActionType.ENABLE);

        EnumListEntry<ActionType> actionSelectorButton = actionEntry.build();
        category.add(actionSelectorButton);


        //Category selector button
        DropdownMenuBuilder<ConfigEntry.Category> typeSelector = entryBuilder.startDropdownMenu(
                Text.translatable("blanket-client-tweaks.config.chooseCategory"),
                DropdownMenuBuilder.TopCellElementBuilder.of(action.category, s -> {
                    try {
                        return ConfigEntry.Category.valueOf(s);
                    } catch(IllegalArgumentException ignore) { }
                    return null;
                }, anEnum -> Text.literal(anEnum.toString())),
                DropdownMenuBuilder.CellCreatorBuilder.of(category1 -> Text.literal(category1.toString())));

        typeSelector.setDefaultValue(ConfigEntry.Category.ALL);
        typeSelector.setSelections(Arrays.stream(ConfigEntry.Category.values()).collect(Collectors.toSet()));


        MutableText categoryTypes = Text.literal("Possible categories:").formatted(Formatting.GOLD);
        for (ConfigEntry.Category categoryEnum : ConfigEntry.Category.values()) {
            categoryTypes.append(Text.literal("\n" + categoryEnum.toString()).formatted(Formatting.BLUE));
        }
        typeSelector.setTooltip(categoryTypes);


        typeSelector.setSaveConsumer(anEnum -> action.category = anEnum);
        DropdownBoxEntry<ConfigEntry.Category> typeSelectorButton = typeSelector.build();

        category.add(typeSelectorButton);


        // Pressable action button
        PressableButtonEntry actionButton = new PressableButtonEntry(Text.translatable("blanket-client-tweaks.config.doBulkAction"), () -> {
            if (typeSelectorButton.getError().isPresent()) return;
            action.action = actionSelectorButton.getValue();
            action.category = typeSelectorButton.getValue();

            MinecraftClient.getInstance().setScreen(new ConfirmScreen(b -> {

                if (b) {
                    for (var pair : parent.getConfigEntries()) {
                        Field field = pair.getLeft();
                        try {
                            if (field.getType().equals(Boolean.TYPE) && (action.category.equals(ConfigEntry.Category.ALL)
                                    || Arrays.stream(field.getAnnotation(ConfigEntry.class).categories()).anyMatch(category12 -> category12 == action.category))) {
                                Field bool = BooleanListEntry.class.getDeclaredField("bool");
                                bool.setAccessible(true);
                                AtomicBoolean atomicBoolean = (AtomicBoolean) bool.get(pair.getRight());
                                atomicBoolean.set(action.action.apply(atomicBoolean.get()));
                            }
                        } catch(Exception ignore) { }
                    }
                    BulkActionScreen.this.client.setScreen(parent);
                } else BulkActionScreen.this.client.setScreen(BulkActionScreen.this);

            }, Text.translatable("blanket-client-tweaks.config.confirmTitle"), Text.translatable(
                    "blanket-client-tweaks.config.confirmText",
                    Text.literal(action.action.toString()).formatted(Formatting.GREEN),
                    Text.literal(action.category.toString()).formatted(Formatting.BLUE)
            )));
        },
                () -> {

                    Text actionText = Text.literal(actionSelectorButton.getValue().toString()).formatted(Formatting.GREEN);
                    if (typeSelectorButton.getError().isEmpty()) {
                        action.category = typeSelectorButton.getValue();
                    }

                    Text category13 = Text.literal(action.category.toString()).formatted(Formatting.BLUE);


                    return Text.translatable("blanket-client-tweaks.config.doBulkAction", actionText, category13);
                });

        //3 nested lambdas :D


        category.add(entryBuilder.startTextDescription(Text.translatable("blanket-client-tweaks.config.bulkDescription")).build());
        category.add(actionButton);


        //field.getType().equals(Boolean.TYPE) && (action.category.equals(ConfigEntry.Category.ALL)
        // || Arrays.stream(field.getAnnotation(ConfigEntry.class).categories()).anyMatch(category12 -> category12 == action.category))
        for (var entry : category) {
            entry.setScreen(this);
        }
    }
}
