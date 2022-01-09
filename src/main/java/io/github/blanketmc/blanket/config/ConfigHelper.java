package io.github.blanketmc.blanket.config;

import io.github.blanketmc.blanket.Config;
import io.github.blanketmc.blanket.ClientFixes;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Pair;
import org.apache.logging.log4j.Level;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public final class ConfigHelper {

    private static final Map<Field, Object> defaults; //create in a static field

    public static int countActiveConfigOptions(Config config) {
        int count = 0;

        Field[] fields = Config.class.getFields();
        for (Field field : fields) {
            try {
                ConfigEntry fieldInfo = field.getAnnotation(ConfigEntry.class);
                if (fieldInfo == null) continue; //this is not a config entry
                if (!Modifier.isStatic(field.getModifiers())) continue; //Every config entry must be static
                if (field.getType().equals(Boolean.TYPE)) {
                    if (field.getBoolean(config)) {
                        count++;
                    }
                } else if (!(getDefaultValue(field).equals(field.get(null)))) { //If field is not equal to default
                    count++;
                }
            } catch(IllegalAccessException ignored) {}
        }
        return count;
    }

    public static void iterateOnConfig(ConfigIterator iterator){
        Field[] fields = Config.class.getFields();
        for (Field field : fields) {
            try {
                ConfigEntry fieldInfo = field.getAnnotation(ConfigEntry.class);
                if (fieldInfo == null) continue; //this is not a config entry
                iterator.acceptConfigEntry(field, fieldInfo);
            } catch(IllegalAccessException ignored) {}
        }
    }

    public static List<Field> getConfigFieldsForCategory(ConfigEntry.Category category){
        List<Field> entries = new ArrayList<>();
        for (Field field : Config.class.getFields()) {
            ConfigEntry fieldInfo = field.getAnnotation(ConfigEntry.class);
            if (fieldInfo == null) continue; //this is not a config entry
            if (category.equals(ConfigEntry.Category.ALL) || Arrays.asList(fieldInfo.categories()).contains(category)) {
                entries.add(field);
            }
        }
        return entries;
    }

    public static List<Pair<Field,ConfigEntry>> getConfigEntriesForCategory(ConfigEntry.Category category){
        List<Pair<Field,ConfigEntry>> entries = new ArrayList<>();
        Field[] fields = Config.class.getFields();
        for (Field field : fields) {
            ConfigEntry fieldInfo = field.getAnnotation(ConfigEntry.class);
            if (fieldInfo == null) continue; //this is not a config entry
            if (Arrays.asList(fieldInfo.categories()).contains(category)) {
                entries.add(new Pair<>(field, fieldInfo));
            }
        }
        return entries;
    }

    public static MutableText getTextComponent(String str, String ifNull) {
        if (str.equals("")) {
            if (ifNull == null) throw new IllegalArgumentException();
            return new LiteralText(ifNull);
        }

        if (str.startsWith("blanket-client-tweaks.")) {
            return new TranslatableText(str);
        }
        return new LiteralText(str);
    }

    public static void saveConfig() {
        Path toConfig = FabricLoader.getInstance().getConfigDir();
        toConfig = toConfig.resolve("blanket_client-fixes.json");

        try (BufferedWriter writer = Files.newBufferedWriter(toConfig, StandardCharsets.UTF_8)){

            ConfigJsonSerializer.serializer.toJson(new Config(), writer); //make GSON find the correct TypeAdapter

        } catch(IOException e){
            ClientFixes.log(Level.ERROR, e.getMessage());
        }
    }

    public static void loadConfig() {
        Path toConfig = FabricLoader.getInstance().getConfigDir();
        toConfig = toConfig.resolve("blanket_client-fixes.json");

        //Create a new config file, if there is no
        if (!toConfig.toFile().isFile()) {
            saveConfig();
        }
        try (BufferedReader reader = Files.newBufferedReader(toConfig, StandardCharsets.UTF_8)){

            ConfigJsonSerializer.serializer.fromJson(reader, Config.class);

        } catch(IOException e){
            ClientFixes.log(Level.ERROR, e.getMessage());
        }
    }

    public static <T> T callClassConstructor(Class<T> clazz) {
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object getDefaultValue(Field configField) {
        ConfigEntry entry = configField.getAnnotation(ConfigEntry.class);
        if (entry == null) throw new IllegalArgumentException(configField + " is not a config entry");

        return defaults.get(configField);
    }

    static {
        defaults = new HashMap<>();
        iterateOnConfig((field, configEntry) -> {
            defaults.put(field, field.get(null));
        });
    }

    public interface ConfigIterator{
        void acceptConfigEntry(Field field, ConfigEntry configEntry) throws IllegalAccessException;
    }
}
