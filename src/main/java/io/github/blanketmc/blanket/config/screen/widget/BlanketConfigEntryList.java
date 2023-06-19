package io.github.blanketmc.blanket.config.screen.widget;

import me.shedaniel.clothconfig2.gui.AbstractConfigScreen;
import me.shedaniel.clothconfig2.gui.ClothConfigScreen;
import me.shedaniel.clothconfig2.gui.widget.DynamicElementListWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BlanketConfigEntryList<E extends DynamicElementListWidget.ElementEntry<E>> extends ClothConfigScreen.ListWidget<E> {

    /**
     * y position
     * remaining time/power
     */
    Map<Integer, Integer> recentElements = new HashMap<>();


    public BlanketConfigEntryList(AbstractConfigScreen screen, MinecraftClient client, int width, int height, int top, int bottom) {
        super(screen, client, width, height, top, bottom, Screen.OPTIONS_BACKGROUND_TEXTURE);
    }

    public void setElements(List<E> elements) {
        this.clearItems();
        elements.forEach((e -> {
            e.setParent(BlanketConfigEntryList.this);
            BlanketConfigEntryList.this.addItem(e);
        }));
        recentElements.clear();
    }

}