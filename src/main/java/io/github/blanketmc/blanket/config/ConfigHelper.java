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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class ConfigHelper {

    public static int countActiveConfigOptions(Config config) {
        int count = 0;
        Config defaults = BlanketConfigScreenProvider.getDefaultsConfig();
        Field[] fields = Config.class.getFields();
        for (Field field : fields) {
            try {
                ConfigEntry fieldInfo = field.getAnnotation(ConfigEntry.class);
                if (fieldInfo == null) continue; //this is not a config entry
                if (field.getType().equals(Boolean.TYPE)) {
                    if (field.getBoolean(config)) {
                        count++;
                    }
                } else if (!(field.get(defaults).equals(field.get(config)))) { //If field is not equal to default
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
            if (Arrays.asList(fieldInfo.categories()).contains(category)) {
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

    public static void saveConfig(Config config) {
        Path toConfig = FabricLoader.getInstance().getConfigDir();
        toConfig = toConfig.resolve("blanket_client-fixes.json");

        try (BufferedWriter writer = Files.newBufferedWriter(toConfig, StandardCharsets.UTF_8)){

            ConfigJsonSerializer.serializer.toJson(config, writer);

        } catch(IOException e){
            ClientFixes.log(Level.ERROR, e.getMessage());
        }
    }

    public static Config loadConfig() {
        Path toConfig = FabricLoader.getInstance().getConfigDir();
        toConfig = toConfig.resolve("blanket_client-fixes.json");

        //Create a new config file, if there is no
        if (!toConfig.toFile().isFile()) {
            Config config = new Config();
            saveConfig(config);
            return config;
        }
        try (BufferedReader reader = Files.newBufferedReader(toConfig, StandardCharsets.UTF_8)){

            return ConfigJsonSerializer.serializer.fromJson(reader, Config.class);

        } catch(IOException e){
            ClientFixes.log(Level.ERROR, e.getMessage());
        }

        return new Config();
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

    public static interface ConfigIterator{
        public void acceptConfigEntry(Field field, ConfigEntry configEntry) throws IllegalAccessException;
    }
}
