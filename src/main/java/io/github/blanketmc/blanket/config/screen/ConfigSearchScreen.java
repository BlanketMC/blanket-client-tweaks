package io.github.blanketmc.blanket.config.screen;

import io.github.blanketmc.blanket.config.ConfigEntry;
import io.github.blanketmc.blanket.config.ConfigHelper;
import io.github.blanketmc.blanket.config.screen.widget.BlanketConfigEntryList;
import io.github.blanketmc.blanket.config.screen.widget.FirstElementAlwaysDisplaySubCategoryEntry;
import me.shedaniel.clothconfig2.api.AbstractConfigEntry;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.gui.AbstractConfigScreen;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Supplier;

@SuppressWarnings({"rawtypes"})
public class ConfigSearchScreen extends AbstractConfigScreen {


    private TextFieldWidget inputWidget;
    private BlanketConfigEntryList entryList;

    private final List<AbstractConfigListEntry> configList;

    public ConfigSearchScreen(Screen parent) {
        super(parent, new TranslatableText("blanket-client-tweaks.config.title"), DrawableHelper.OPTIONS_BACKGROUND_TEXTURE);

        configList = fillConfigList();
    }

    @Override
    public void onClose() {
        assert this.client != null;
        this.client.setScreen(parent);
    }

    @Override
    protected void init() {
        super.init();
        inputWidget = new TextFieldWidget(this.textRenderer,100, 0, this.width/2, 20, new LiteralText("Search"));
        this.addSelectableChild(inputWidget);

        entryList = new BlanketConfigEntryList(this, client, this.width, this.height - 40, 20,this.height - 20);
        //entryList.setLeftPos(20);

        this.addSelectableChild(entryList);

        this.setInitialFocus(inputWidget);
        entryList.setElements(configList);

    }

    private List<AbstractConfigListEntry> fillConfigList() {
        List<AbstractConfigListEntry> configList = new ArrayList<>();
        ConfigHelper.iterateOnConfig(((field, configEntry) -> addEntry(configList, field, configEntry)));
        return configList;
    }

    private void addEntry(List<AbstractConfigListEntry> configList, Field field, ConfigEntry configEntry) throws IllegalAccessException {
        AbstractConfigListEntry entry = ScreenHelper.createConfigEntry(field);
        entry.setScreen(this);
        if (configEntry.extraProperties().length != 0) {
            List<AbstractConfigListEntry> propertyEntries = ScreenHelper.createExtraConfigEntries(configEntry.extraProperties());
            if (!propertyEntries.isEmpty()){
                var listEntry = new FirstElementAlwaysDisplaySubCategoryEntry(entry, propertyEntries,this);

                configList.add(listEntry);
                return;
            }
        }
        configList.add(entry);
    }


    @Override
    public Map<Text, List<AbstractConfigEntry<?>>> getCategorizedEntries() {

        var map = new HashMap<Text, List<AbstractConfigEntry<?>>>();

        List<AbstractConfigEntry<?>> list = this.configList.stream().collect(
                (Supplier<List<AbstractConfigEntry<?>>>) ArrayList::new,
                List::add,
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

        super.render(matrices, mouseX, mouseY, delta);
    }
}
