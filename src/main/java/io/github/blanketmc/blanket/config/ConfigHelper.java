package io.github.blanketmc.blanket.config;

import io.github.blanketmc.blanket.Config;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.lang.reflect.Field;

public final class ConfigHelper {

    public static void iterateOnConfig(ConfigIterator iterator){
        Field[] fields = Config.class.getFields();
        for (var field : fields) {
            try {
                ConfigEntry fieldInfo = field.getAnnotation(ConfigEntry.class);
                if (fieldInfo == null) continue; //this is not a config entry

                iterator.acceptConfigEntry(field, fieldInfo);
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

    public static interface ConfigIterator{
        public void acceptConfigEntry(Field field, ConfigEntry configEntry) throws IllegalAccessException;
    }
}
