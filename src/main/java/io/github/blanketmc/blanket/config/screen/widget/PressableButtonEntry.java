package io.github.blanketmc.blanket.config.screen.widget;

import com.google.common.collect.Lists;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.Window;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class PressableButtonEntry extends AbstractConfigListEntry<Object> {
    private final ButtonWidget actionButton;
    private final Supplier<Text> displayTextSupplier;
    public PressableButtonEntry(Text fieldName, Runnable onPress, Supplier<Text> displayTextSupplier) {
        super(fieldName, false);

        this.displayTextSupplier = displayTextSupplier;

        this.actionButton = ButtonWidget.builder(NarratorManager.EMPTY, (widget) -> onPress.run()).dimensions(0, 0, 150, 20).build();
    }

    @Override
    public void render(DrawContext graphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isHovered, float delta) {
        super.render(graphics, index, y, x, entryWidth, entryHeight, mouseX, mouseY, isHovered, delta);
        Window window = MinecraftClient.getInstance().getWindow();
        this.actionButton.active = this.isEditable();
        this.actionButton.setY(y);
        this.actionButton.setMessage(Text.literal("[X]").formatted(Formatting.RED));
        Text displayedFieldName = this.displayTextSupplier.get();
        if (MinecraftClient.getInstance().textRenderer.isRightToLeft()) {
            graphics.drawCenteredTextWithShadow(MinecraftClient.getInstance().textRenderer, displayedFieldName.asOrderedText(), (window.getScaledWidth() - x - MinecraftClient.getInstance().textRenderer.getWidth(displayedFieldName)), (y + 6), 16777215);
            this.actionButton.setX(x + 2);
        } else {
            graphics.drawCenteredTextWithShadow(MinecraftClient.getInstance().textRenderer, displayedFieldName.asOrderedText(), x, (y + 6), this.getPreferredTextColor());
            this.actionButton.setX(x + entryWidth - 150);
        }

        this.actionButton.setWidth(150 - 2);
        this.actionButton.render(graphics, mouseX, mouseY, delta);
    }

    @Override
    public Object getValue() {
        return null;
    }

    @Override
    public Optional<Object> getDefaultValue() {
        return Optional.empty();
    }

    @Override
    public void save() {

    }

    @Override
    public List<? extends Selectable> narratables() {
        return new ArrayList<>();
    }

    @Override
    public List<? extends Element> children() {
        return Lists.newArrayList(this.actionButton);
    }
}
