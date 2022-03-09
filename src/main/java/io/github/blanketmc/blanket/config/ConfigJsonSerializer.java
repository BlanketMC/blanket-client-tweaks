package io.github.blanketmc.blanket.config;

import com.google.gson.*;
import io.github.blanketmc.blanket.Config;
import io.github.blanketmc.blanket.ClientFixes;
import org.apache.logging.log4j.Level;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

public class ConfigJsonSerializer implements JsonSerializer<Config>, JsonDeserializer<Config> {
    public static final Gson serializer;

    @Override
    public Config deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject node = json.getAsJsonObject();
        ConfigHelper.iterateOnConfig((field, configEntry) -> {

            String entryName = configEntry.name().equals("") ? field.getName() : configEntry.name();
            if (node.has(entryName)) {

                JsonObject configNode = node.get(entryName).getAsJsonObject();
                if (!configNode.has("value")) return;

                fromJson(field, configNode.get("value"));
                if (configEntry.extraProperties().length != 0 && configNode.has("extra")) {
                    JsonObject extraNode = configNode.get("extra").getAsJsonObject();
                    for (String extraProperty : configEntry.extraProperties()) {
                        if (extraNode.has(extraProperty)) {
                            try {
                                fromJson(Config.class.getField(extraProperty), extraNode.get(extraProperty));
                            } catch(NoSuchFieldException ignore) {}
                        }
                    }
                }
            }
        });
        return new Config();
    }

    @Override
    public JsonElement serialize(Config src, Type typeOfSrc, JsonSerializationContext context) {
        var node = new JsonObject();

        node.addProperty("aboutConfig", "Blanket config: The mod only cares about the *value* property, you can edit, delete the others.");
        node.addProperty("aboutConfig2", "But if you save the config from the mod, it will override this file.");
        node.addProperty("aboutConfig3", "Good luck!");

        ConfigHelper.iterateOnConfig((field, configEntry) -> {
            node.add(configEntry.name().equals("") ? field.getName() : configEntry.name(), writeField(src, field, configEntry));
        });
        return node;
    }

    public JsonElement writeField(Config ignored, Field field, ConfigEntry configEntry) throws IllegalAccessException {
        var node = new JsonObject();

        if (!configEntry.displayName().equals("")) {
            node.addProperty("name", configEntry.displayName());
        }

        if (!configEntry.description().equals("")) {
            node.addProperty("description", configEntry.description());
        }

        var issues = new JsonArray();
        for (String issue : configEntry.issues()) {
            issues.add(issue);
        }
        node.add("issues", issues);

        var categories = new JsonArray();
        for (ConfigEntry.Category category : configEntry.categories()) {
            categories.add(category.toString());
        }
        node.add("categories", categories);


        node.add("value", toJson(field));

        if (configEntry.extraProperties().length != 0) {
            JsonObject extraProps = new JsonObject();
            for (String extraProperty : configEntry.extraProperties()) {
                try {
                    extraProps.add(extraProperty, toJson(Config.class.getField(extraProperty)));
                } catch(NoSuchFieldException ignore) {}
            }
            node.add("extra", extraProps);
        }

        return node;
    }

    protected static JsonElement toJson(Field field) throws IllegalAccessException {
        Class<?> type = field.getType();

        if (type.equals(Boolean.TYPE)) {
            return new JsonPrimitive((boolean)field.get(null));
        } else if (type.equals(Float.TYPE)) {
            return new JsonPrimitive((float)field.get(null));
        } else if (type.equals(Double.TYPE)) {
            return new JsonPrimitive((double)field.get(null));
        } else if (type.equals(Integer.TYPE)) {
            return new JsonPrimitive((int)field.get(null));
        } else if (type.equals(Long.TYPE)) {
            return new JsonPrimitive((long)field.get(null));
        } else if (type.equals(String.class)) {
            return new JsonPrimitive((String) field.get(null));
        } else if (type.isEnum()) {
            try {
                Method toString = type.getMethod("toString");
                return new JsonPrimitive((String)toString.invoke(field.get(null)));
            } catch(NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        throw new IllegalArgumentException("unknown type: " + type + " for field: " + field.getName());
    }

    protected static void fromJson(Field field, JsonElement configNode) throws IllegalAccessException {
        Class<?> type = field.getType();

        if (type.equals(Boolean.TYPE)) {
            if (!configNode.getAsJsonPrimitive().isBoolean()) return;
            field.set(null, configNode.getAsBoolean());
        } else if (type.equals(Float.TYPE)) {
            if (!configNode.getAsJsonPrimitive().isNumber()) return;
            field.set(null, configNode.getAsFloat());
        } else if (type.equals(Double.TYPE)) {
            if (!configNode.getAsJsonPrimitive().isNumber()) return;
            field.set(null, configNode.getAsDouble());
        } else if (type.equals(Integer.TYPE)) {
            if (!configNode.getAsJsonPrimitive().isNumber()) return;
            field.set(null, configNode.getAsInt());
        } else if (type.equals(Long.TYPE)) {
            if (!configNode.getAsJsonPrimitive().isNumber()) return;
            field.set(null, configNode.getAsLong());
        } else if (type.equals(String.class)) {
            if (!configNode.getAsJsonPrimitive().isString()) return;
            field.set(null, configNode.getAsString());
        } else if (type.isEnum()) {
            if (!configNode.getAsJsonPrimitive().isString()) return;
            try { //now, this is tricky
                Method valueOf = type.getMethod("valueOf", String.class);
                field.set(null, valueOf.invoke(null, configNode.getAsString()));
            } catch(NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
            }
        } else {
            ClientFixes.log(Level.ERROR, "Config: " + field.getName() + " can not be imported: Unknown type", true);
        }
    }

    static {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        builder.registerTypeAdapter(Config.class, new ConfigJsonSerializer());

        serializer = builder.create();
    }

    /*
    it will be:

    {
        "option 1" : {
            "name" : "The name of the first option",
            "description" : "some description",
            "issues" : ["MC-1","MC-111"],
            "categories" : ["GENERAL", "BUGFIX", "RECOMMENDED"],
            "value" : 42,
            "extra" : {
                "extraProperty1" : 1,
                "extraProperty2" : true
            }
         },
         ...
    }

    comments, name fields will be ignored when loading the config, but it will make config editing way easier.
     */
}
