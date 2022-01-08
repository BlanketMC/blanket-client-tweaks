package io.github.blanketmc.blanket.config;

import io.github.blanketmc.blanket.Config;
import io.github.blanketmc.blanket.FabricModInitializer;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.apache.logging.log4j.Level;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
public final class ConfigHelper {

    private static final Config defaults = new Config();

    public static void optionFilter(Config config) {
        Field[] fields = config.getClass().getFields();
        for (Field field : fields) {
            try {
                if (!(field.get(field) instanceof Boolean)) continue;

                ConfigEntry entry = field.getAnnotation(ConfigEntry.class);

                for (ConfigEntry.Category category : entry.categories()) {
                    if (category == ConfigEntry.Category.BUGFIX) {
                        field.set(field, true);
                        break;
                    }
                }

            } catch(IllegalAccessException ignore) { }
        }
    }

    public static Screen getScreen(Screen parent, Config config) {
        ConfigBuilder builder = ConfigBuilder.create().setParentScreen(parent).setTitle(new TranslatableText("blanket-client-tweaks.config.title"));
        builder.setSavingRunnable(() -> saveConfig(config));

        //Config entry category
        ConfigCategory general = builder.getOrCreateCategory(new TranslatableText("blanket-client-tweaks.config.general")); //we can ignore the title, until we have more categories
        addEntriesToCategory(general, builder.entryBuilder(), config);

        return builder.build();
    }

    private static void addEntriesToCategory(ConfigCategory category, ConfigEntryBuilder entryBuilder, Config config) {

        Field[] fields = config.getClass().getFields();
        for (var field : fields) {
            try {
                ConfigEntry fieldInfo = field.getAnnotation(ConfigEntry.class);
                if (fieldInfo == null) continue; //this is not a config entry

                var type = field.getType();
                if (type.equals(Boolean.TYPE)) {

                    var entry = entryBuilder.startBooleanToggle(getTextComponent(fieldInfo.displayName(), field.getName()), field.getBoolean(config));

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
                    if (!fieldInfo.description().equals("")) {
                        entry.setTooltip(getTextComponent(fieldInfo.description(), null));
                    }

                    category.addEntry(entry.build());

                } else if (type.isEnum()) {

                    var clazz = (Class<Enum<?>>) type;

                    Object obj = field.get(config);
                    var entry = entryBuilder.startEnumSelector(getTextComponent(fieldInfo.displayName(), field.getName()), clazz, clazz.cast(obj));

                    //default value using mirror class
                    Object defVal = field.get(defaults);
                    entry.setDefaultValue(clazz.cast(defVal));

                    //Description
                    if (!fieldInfo.description().equals("")) {
                        entry.setTooltip(getTextComponent(fieldInfo.description(), null));
                    }

                    category.addEntry(entry.build());

                } else {
                    FabricModInitializer.log(Level.ERROR, "Config: " + field.getName() + " can not be displayed: Unknown type", true);
                }
            } catch(IllegalAccessException ignored) {}
        }
    }

    public static Text getTextComponent(String str, String ifNull) {
        if (str.equals("")) {
            if (ifNull == null) throw new IllegalArgumentException();
            return new LiteralText(ifNull);
        }

        if (str.startsWith("blanket-client-tweaks.")) {
            return new TranslatableText(str);
        }
        return new LiteralText(str);
    }

    public static void saveConfig(Config config) {
        //TODO
    }

    public static Config loadConfig() {
        //TODO
        return new Config();
    }

    //public record AnnotatedEntry (Field field, ConfigEntry annotation) {}
}
