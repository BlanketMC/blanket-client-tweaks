package io.github.blanketmc.blanket.config.screen.widget;

import me.shedaniel.clothconfig2.api.AbstractConfigEntry;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.AbstractConfigScreen;
import me.shedaniel.clothconfig2.gui.entries.SubCategoryListEntry;
import me.shedaniel.clothconfig2.gui.widget.DynamicEntryListWidget;
import me.shedaniel.math.Rectangle;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

@SuppressWarnings({"rawtypes", "unchecked"})
@ApiStatus.Experimental
public class FirstElementAlwaysDisplaySubCategoryEntry extends AbstractConfigListEntry {

    private final SubCategoryListEntry extraConfigListEntry;
    private final AbstractConfigEntry configEntry;


    public FirstElementAlwaysDisplaySubCategoryEntry(AbstractConfigEntry configListEntry, List<AbstractConfigListEntry> extraConfigEntries, AbstractConfigScreen screen) {
        super(Text.literal(""), false);
        this.configEntry = configListEntry;

        var listBuilder = ConfigEntryBuilder.create().startSubCategory(Text.literal(""), extraConfigEntries);
        this.extraConfigListEntry = listBuilder.build();
        this.extraConfigListEntry.setScreen(screen);

    }

    @Override
    public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isHovered, float delta) {
        //Render both at the same starting position
        extraConfigListEntry.render(matrices, index, y, x, entryWidth, entryHeight, mouseX, mouseY, isHovered, delta);
        configEntry.render(matrices, index, y, x, entryWidth, entryHeight, mouseX, mouseY, isHovered, delta);
    }

    @Override
    public void setParent(DynamicEntryListWidget parent) {
        super.setParent(parent);
        extraConfigListEntry.setParent(parent);
        configEntry.setParent(parent);
    }



    @Override
    public Rectangle getEntryArea(int x, int y, int entryWidth, int entryHeight) {
        return super.getEntryArea(x, y, entryWidth, entryHeight);
    }

    @Override
    public Text getDisplayedFieldName() {
        return configEntry.getDisplayedFieldName();
    }

    @Override
    public Optional<Text> getError() {
        Optional<Text> error = configEntry.getError();
        return error.isPresent() ? error : extraConfigListEntry.getError();
    }

    @Override
    public boolean isEdited() {
        return configEntry.isEdited()
                || extraConfigListEntry.isEdited();
    }

    @Override
    public int getItemHeight() {
        return Math.max(extraConfigListEntry.getItemHeight(), configEntry.getItemHeight());
    }

    @Override
    public Object getValue() {
        //return configEntry.getValue();
        return null;
    }

    @Override
    public Optional getDefaultValue() {
        return configEntry.getDefaultValue();
    }

    @Override
    public void save() {
        configEntry.save();
        extraConfigListEntry.save();
    }



    @Override
    public boolean isRequiresRestart() {
        return configEntry.isRequiresRestart() || extraConfigListEntry.isRequiresRestart();
    }

    @Override
    public List<? extends Selectable> narratables() {
        return children().stream().collect(
                LinkedList::new,
                (BiConsumer<List<Selectable>, Element>) (selectables, element) -> {
                    if (element instanceof Selectable selectableElement) {
                        selectables.add(selectableElement);
                    }
                },
                List::addAll
        );
    }

    @Override
    public List<? extends Element> children() {
        List<Element> children = new java.util.ArrayList<>();
        children.add(configEntry);
        children.add(extraConfigListEntry);
        return children;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        return super.charTyped(chr, modifiers);
    }

    @Override
    public void setInitialFocus(@Nullable Element element) {
        super.setInitialFocus(element);
    }

    @Override
    public void focusOn(@Nullable Element element) {
        super.focusOn(element);
        configEntry.focusOn(element);
        extraConfigListEntry.focusOn(element);
    }

    @Override
    public boolean changeFocus(boolean lookForwards) {
        return super.changeFocus(lookForwards);
    }

    @Override
    public void setFocused(Element element_1) {
        super.setFocused(element_1);
        //configEntry.setFocused(element_1);
    }

    @Override
    public void updateSelected(boolean isSelected) {
        super.updateSelected(isSelected);
        configEntry.updateSelected(isSelected && getFocused() == configEntry);
        extraConfigListEntry.updateSelected(isSelected && getFocused() == extraConfigListEntry);
    }

    @Override
    public Text getFieldName() {
        return configEntry.getFieldName();
    }
}
