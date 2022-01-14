package io.github.blanketmc.blanket.config.screen;

import io.github.blanketmc.blanket.config.ConfigEntry;
import io.github.blanketmc.blanket.config.screen.widget.BlanketConfigEntryList;
import me.shedaniel.clothconfig2.api.AbstractConfigEntry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.AbstractConfigScreen;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

import java.util.*;
import java.util.function.Supplier;

public class FilterScreen extends AbstractConfigScreen {

    private BlanketConfigEntryList entries;
    private List<Drawable> drawables = new ArrayList<>();

    private ButtonWidget quitButton;
    private ButtonWidget resetButton;

    private final List<AbstractConfigEntry> entryList;
    private /*static*/ final Set<ConfigEntry.Category> filteredCategories = new HashSet<>();
    private FilterMode filterMode = FilterMode.INCLUDE;

    protected FilterScreen(BlanketConfigScreen parent) {
        super(parent, new TranslatableText("blanket-client-tweaks.config.filter"), DrawableHelper.OPTIONS_BACKGROUND_TEXTURE);

        for (var category : ConfigEntry.Category.values()) {
            if (category == ConfigEntry.Category.ALL) continue;
            filteredCategories.add(category);
        }

        entryList = createEntries();
    }

    @Override
    protected void init() {
        super.init();
        entries = new BlanketConfigEntryList(this, client, this.width, this.height - 60, 30,this.height - 30);
        entries.setElements(entryList);

        this.addDrawableChild(entries);


        int buttonWidths = Math.min(200, (this.width - 50 - 12) / 3);
        this.addDrawableChild(this.quitButton = new ButtonWidget((this.width + buttonWidths) / 2 - buttonWidths, this.height - 26, buttonWidths, 20, new TranslatableText("blanket-client-tweaks.config.filterMode"), (widget) -> {
            this.saveAll(true);
            this.quit();
        }));
        /*
        this.addDrawableChild(this.resetButton = new ButtonWidget(this.width / 4 - buttonWidths/2, 10, buttonWidths, 20, new TranslatableText("gui.resetAll"), (widget) -> {
            for (var entry : entryList) {
                entry.
            }
        }));
         */ // TODO filter reset button
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);

        entries.render(matrices, mouseX, mouseY, delta);

        for (Drawable drawable : drawables) {
            drawable.render(matrices, mouseX, mouseY, delta);
        }

        drawCenteredText(matrices, this.textRenderer, this.title, this.width / 4 * 3, 10, 16777215);
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public void saveAll(boolean openOtherScreens) {
        ((BlanketConfigScreen)this.parent).categoryFilter = categories -> {
            for (ConfigEntry.Category category : categories) {
                if (FilterScreen.this.filteredCategories.contains(category)) {
                    return filterMode == FilterMode.INCLUDE;
                }
            }
            return filterMode == FilterMode.EXCLUDE;
        };
        super.saveAll(openOtherScreens);
    }

    @Override
    public Map<Text, List<AbstractConfigEntry<?>>> getCategorizedEntries() {
        Map<Text, List<AbstractConfigEntry<?>>> map = new HashMap<>();
        List<AbstractConfigEntry<?>> list = this.entryList.stream().collect(
                (Supplier<List<AbstractConfigEntry<?>>>) ArrayList::new,
                List::add,
                List::addAll
        );
        map.put(new LiteralText("bulk"), list);
        return map;
    }


    private List<AbstractConfigEntry> createEntries() {
        List<AbstractConfigEntry> entries = new ArrayList<>();

        ConfigEntryBuilder entryBuilder = ConfigEntryBuilder.create();
        var mode = entryBuilder.startEnumSelector(new TranslatableText("blanket-client-tweaks.config.filterMode"),
                FilterMode.class,
                this.filterMode);
        mode.setSaveConsumer(filterMode -> FilterScreen.this.filterMode = filterMode);

        entries.add(mode.build());

        for (ConfigEntry.Category category : ConfigEntry.Category.values()) {
            if (category == ConfigEntry.Category.ALL) continue;

            var toggleBuilder = entryBuilder.startBooleanToggle(new LiteralText(category.toString()).formatted(Formatting.BLUE),
                    filteredCategories.contains(category));

            toggleBuilder.setSaveConsumer(aBoolean -> {
                if (aBoolean) {
                    filteredCategories.add(category);
                } else {
                    filteredCategories.remove(category);
                }
            });
            entries.add(toggleBuilder.build());
        }


        for (var entry : entries) entry.setScreen(this);
        return entries;
    }

    private enum FilterMode {
        INCLUDE,
        EXCLUDE,
    }

    /*
    static {
        filteredCategories = new HashSet<>();
        for (var category : ConfigEntry.Category.values()) {
            if (category != ConfigEntry.Category.ALL) {
                filteredCategories.add(category);
            }
        }
    }

     */
}
