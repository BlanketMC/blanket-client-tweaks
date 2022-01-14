package io.github.blanketmc.blanket.config.screen.util;

import io.github.blanketmc.blanket.ClientFixes;
import io.github.blanketmc.blanket.Config;
import io.github.blanketmc.blanket.config.ConfigEntry;
import io.github.blanketmc.blanket.config.ConfigHelper;
import io.github.blanketmc.blanket.config.ExtraProperty;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.apache.logging.log4j.Level;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


public final class ScreenHelper {


    public static AbstractConfigListEntry<?> createConfigEntry(Field field) throws IllegalAccessException {
        return new ConfigEntryElementBuilder(field).createConfigEntry();
    }

    @SuppressWarnings("rawtypes")
    public static List<AbstractConfigListEntry> createExtraConfigEntries(String[] properties) {
        List<AbstractConfigListEntry> listEntries = new ArrayList<>();
        for (String property : properties) {
            try {
                Field field = Config.class.getField(property);
                if (field.isAnnotationPresent(ExtraProperty.class)) {
                    listEntries.add(new ExtraPropertyElementBuilder(field).createConfigEntry());
                } else {
                    throw new NoSuchFieldException(property);
                }
            } catch(NoSuchFieldException | IllegalAccessException ignore) {
                ClientFixes.log(Level.ERROR, "No extraProperty:" + property);
            }
        }
        return listEntries;
    }

    public static Text fancyDescription(String desc, ConfigEntry.Category[] categories, String[] issues) {
        MutableText description = new LiteralText("");
        if (!desc.equals("")) {
            description = ConfigHelper.getTextComponent(desc, null);

            description = description.formatted(Formatting.YELLOW).append(new LiteralText("\n"));

            if (issues.length == 0) description.append(new LiteralText("\n"));
        }

        if (issues.length > 0) {
            description.append(new LiteralText("Fixes:\n").formatted(Formatting.DARK_PURPLE));

            Iterator<String> iterator = Arrays.stream(issues).iterator();
            while (iterator.hasNext()) {
                String issue = iterator.next();
                description.append(new LiteralText(issue).formatted(Formatting.DARK_AQUA));
                if (iterator.hasNext()) {
                    description.append(new LiteralText(" + ").formatted(Formatting.GOLD));
                }
            }
            description.append("\n\n");
        }

        description.append(new LiteralText("Categories:\n").formatted(Formatting.LIGHT_PURPLE));

        Iterator<ConfigEntry.Category> iterator = Arrays.stream(categories).iterator();
        while (iterator.hasNext()) {

            ConfigEntry.Category category = iterator.next();
            description.append(new LiteralText(category.toString()).formatted(Formatting.BLUE));

            if (iterator.hasNext()) {
                description.append(new LiteralText(" + ").formatted(Formatting.GOLD));
            }
        }
        return description;
    }

}
