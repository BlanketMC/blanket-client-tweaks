package io.github.blanketmc.blanket.config.screen;

import com.google.common.collect.Lists;
import io.github.blanketmc.blanket.config.ConfigEntry;
import io.github.blanketmc.blanket.config.ConfigHelper;
import io.github.blanketmc.blanket.config.screen.util.ScreenHelper;
import io.github.blanketmc.blanket.config.screen.widget.BlanketConfigEntryList;
import io.github.blanketmc.blanket.config.screen.widget.FirstElementAlwaysDisplaySubCategoryEntry;
import me.shedaniel.clothconfig2.api.AbstractConfigEntry;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.gui.AbstractConfigScreen;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Pair;

import java.lang.reflect.Field;
import java.util.*;

import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings({"rawtypes", "unchecked"})
public class BlanketConfigScreen extends AbstractConfigScreen {


    private TextFieldWidget inputWidget;
    private BlanketConfigEntryList entryList;

    private final List<Pair<Field, AbstractConfigListEntry>> configList;

    //For the search filter
    private String searchString = "";
    //For the more advanced type filter
    public Function<ConfigEntry.Category[], Boolean> categoryFilter = field -> true;

    private int sortOrder = 0;
    private List<Drawable> drawables = new ArrayList<>();

    private ButtonWidget saveButton;
    private ButtonWidget quitButton;

    private final FilterScreen categorySelectorScreen;

    public BlanketConfigScreen(Screen parent) {
        super(parent, new TranslatableText("blanket-client-tweaks.config.title"), DrawableHelper.OPTIONS_BACKGROUND_TEXTURE);

        configList = fillConfigList();
        categorySelectorScreen = new FilterScreen(this);
    }

    @Override
    public void close() {
        assert this.client != null;
        this.client.setScreen(parent);
    }

    @Override
    protected void init() {
        drawables = new ArrayList<>();
        super.init();

        entryList = new BlanketConfigEntryList(this, client, this.width, this.height - 60, 30, this.height - 30);
        //entryList.setLeftPos(20);

        int menuPos = 40;
        inputWidget = new TextFieldWidget(this.textRenderer, menuPos, 5, this.width / 3, 20, new LiteralText("Search"));
        menuPos += this.width / 3 + 10;
        this.addSelectableChild(inputWidget);

        inputWidget.setChangedListener(this::setSearch);
        inputWidget.setText(searchString); //this will invoke the changed listener :D



        this.addSelectableChild(entryList);

        this.setInitialFocus(inputWidget);
        //entryList.setElements(configList);
        ButtonWidget filterButtonWidget = new ButtonWidget(menuPos, 5, 40, 20, new TranslatableText("blanket-client-tweaks.config.filter"), (button) -> {
            this.client.setScreen(categorySelectorScreen);
        });
        menuPos += 50;
        this.addSelectableChild(filterButtonWidget);
        this.drawables.add(filterButtonWidget);


        ButtonWidget bulkButtonWidget = new ButtonWidget(menuPos, 5, 80, 20, new TranslatableText("blanket-client-tweaks.config.bulk"), (button) -> {
            this.client.setScreen(new BulkActionScreen(this));
        });
        this.addSelectableChild(bulkButtonWidget);
        this.drawables.add(bulkButtonWidget);



        int buttonWidths = Math.min(200, (this.width - 50 - 12) / 3);
        this.addDrawableChild(this.quitButton = new ButtonWidget(this.width / 2 - buttonWidths - 3, this.height - 26, buttonWidths, 20, this.isEdited() ? new TranslatableText("text.cloth-config.cancel_discard") : new TranslatableText("gui.cancel"), (widget) -> {
            this.quit();
        }));
        this.addDrawableChild(this.saveButton = new ButtonWidget(this.width / 2 + 3, this.height - 26, buttonWidths, 20, NarratorManager.EMPTY, (button) -> {
            this.saveAll(true);
        }) {
            public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
                boolean hasErrors = false;

                for (List<AbstractConfigEntry<?>> abstractConfigEntries : Lists.newArrayList(BlanketConfigScreen.this.getCategorizedEntries().values())) {
                    List<AbstractConfigEntry<?>> entries = abstractConfigEntries;

                    for (AbstractConfigEntry<?> abstractConfigEntry : entries) {
                        AbstractConfigEntry<?> entry = abstractConfigEntry;
                        if (entry.getConfigError().isPresent()) {
                            hasErrors = true;
                            break;
                        }
                    }

                    if (hasErrors) {
                        break;
                    }
                }

                this.active = BlanketConfigScreen.this.isEdited() && !hasErrors;
                this.setMessage(hasErrors ? new TranslatableText("text.cloth-config.error_cannot_save") : new TranslatableText("text.cloth-config.save_and_done"));
                super.render(matrices, mouseX, mouseY, delta);
            }
        });
        this.drawables.add(saveButton);
        this.drawables.add(quitButton);

    }


    public void setSearch(String str) {
        searchString = str;
        List<AbstractConfigListEntry> entriesToAdd = new ArrayList<>();
        for(var entry : configList) {
            ConfigEntry configEntry = entry.getLeft().getAnnotation(ConfigEntry.class);
            String entryName = !configEntry.displayName().equals("") ? configEntry.displayName() : entry.getLeft().getName();

            if (entryName.toLowerCase().contains(searchString.toLowerCase())) {
                if (this.categoryFilter.apply(configEntry.categories())) {
                    entriesToAdd.add(entry.getRight());
                }
            }
        }

        if (sortOrder != 0) {
            entriesToAdd.sort((o1, o2) -> o1.getFieldName().getString().toLowerCase().compareTo(o2.getFieldName().getString().toLowerCase()) * sortOrder);
        }
        entryList.setElements(entriesToAdd);
    }

    /**
     * -1: backwards
     * 0: default
     * 1: forward
     * @param newOrder new sort order
     */
    public void setSortOrder(int newOrder) {
        if (newOrder > 1 || newOrder < -1) {
            throw new IllegalArgumentException();
        }
        this.sortOrder = newOrder;
    }

    private List<Pair<Field, AbstractConfigListEntry>> fillConfigList() {
        List<Pair<Field, AbstractConfigListEntry>> configList = new ArrayList<>();
        ConfigHelper.iterateOnConfig(((field, configEntry) -> addEntry(configList, field, configEntry)));
        return configList;
    }

    public List<Pair<Field, AbstractConfigListEntry>> getConfigEntries() {
        return this.configList;
    }

    private void addEntry(List<Pair<Field, AbstractConfigListEntry>> configList, Field field, ConfigEntry configEntry) throws IllegalAccessException {
        AbstractConfigListEntry entry = ScreenHelper.createConfigEntry(field);
        entry.setScreen(this);
        if (configEntry.extraProperties().length != 0) {
            List<AbstractConfigListEntry> propertyEntries = ScreenHelper.createExtraConfigEntries(configEntry.extraProperties());
            if (!propertyEntries.isEmpty()){
                var listEntry = new FirstElementAlwaysDisplaySubCategoryEntry(entry, propertyEntries,this);

                configList.add(new Pair(field, listEntry));
                return;
            }
        }
        configList.add(new Pair(field, entry));
    }

    @Override
    public void saveAll(boolean openOtherScreens) {
        super.saveAll(openOtherScreens);
        ConfigHelper.saveConfig();
    }

    @Override
    public Map<Text, List<AbstractConfigEntry<?>>> getCategorizedEntries() {

        var map = new HashMap<Text, List<AbstractConfigEntry<?>>>();

        List<AbstractConfigEntry<?>> list = this.configList.stream().collect(
                (Supplier<List<AbstractConfigEntry<?>>>) ArrayList::new,
                (abstractConfigEntries, fieldAbstractConfigListEntryEntry) -> abstractConfigEntries.add(fieldAbstractConfigListEntryEntry.getRight()),
                List::addAll
        );

        map.put(new LiteralText("blanket"), list);

        return map;
    }


    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);

        this.entryList.render(matrices, mouseX, mouseY, delta);

        //The search box
        this.inputWidget.render(matrices, mouseX, mouseY, delta);

        for(Drawable drawable : drawables) {
            drawable.render(matrices, mouseX, mouseY, delta);
        }

        super.render(matrices, mouseX, mouseY, delta);
    }
}
