package io.github.blanketmc.blanket.config.screen.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import me.shedaniel.clothconfig2.api.AbstractConfigEntry;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.gui.AbstractConfigScreen;
import me.shedaniel.clothconfig2.gui.ClothConfigScreen;
import me.shedaniel.clothconfig2.gui.widget.DynamicElementListWidget;
import me.shedaniel.clothconfig2.gui.widget.DynamicEntryListWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;

import java.util.*;


public class BlanketConfigEntryList<E extends DynamicElementListWidget.ElementEntry<E>> extends ClothConfigScreen.ListWidget<E> {

    /**
     * y position
     * remaining time/power
     */
    Map<Integer, Integer> recentElements = new HashMap<>();


    public BlanketConfigEntryList(AbstractConfigScreen screen, MinecraftClient client, int width, int height, int top, int bottom) {
        super(screen, client, width, height, top, bottom, OPTIONS_BACKGROUND_TEXTURE);
    }

    public void setElements(List<E> elements) {
        this.clearItems();
        elements.forEach(BlanketConfigEntryList.this::addItem);
        recentElements.clear();
    }

}