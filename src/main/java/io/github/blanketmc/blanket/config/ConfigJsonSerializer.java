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


                Class<?> type = field.getType();

                if (type.equals(Boolean.TYPE)) {
                    if (!configNode.get("value").getAsJsonPrimitive().isBoolean()) return;
                    field.set(null, configNode.get("value").getAsBoolean());
                } else if (type.equals(Float.TYPE)) {
                    if (!configNode.get("value").getAsJsonPrimitive().isNumber()) return;
                    field.set(null, configNode.get("value").getAsFloat());
                } else if (type.equals(Double.TYPE)) {
                    if (!configNode.get("value").getAsJsonPrimitive().isNumber()) return;
                    field.set(null, configNode.get("value").getAsDouble());
                } else if (type.equals(Integer.TYPE)) {
                    if (!configNode.get("value").getAsJsonPrimitive().isNumber()) return;
                    field.set(null, configNode.get("value").getAsInt());
                } else if (type.equals(Long.TYPE)) {
                    if (!configNode.get("value").getAsJsonPrimitive().isNumber()) return;
                    field.set(null, configNode.get("value").getAsLong());
                } else if (type.equals(String.class)) {
                    if (!configNode.get("value").getAsJsonPrimitive().isString()) return;
                    field.set(null, configNode.get("value").getAsString());
                } else if (type.isEnum()) {
                    if (!configNode.get("value").getAsJsonPrimitive().isString()) return;
                    try { //now, this is tricky
                        Method valueOf = type.getMethod("valueOf", String.class);
                        field.set(null, valueOf.invoke(null, configNode.get("value").getAsString()));
                    } catch(NoSuchMethodException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                } else {
                    ClientFixes.log(Level.ERROR, "Config: " + field.getName() + " can not be imported: Unknown type", true);
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


        Class<?> type = field.getType();

        if (type.equals(Boolean.TYPE)) {
            node.addProperty("value", (boolean)field.get(null));
        } else if (type.equals(Float.TYPE)) {
            node.addProperty("value", (float)field.get(null));
        } else if (type.equals(Double.TYPE)) {
            node.addProperty("value", (double)field.get(null));
        } else if (type.equals(Integer.TYPE)) {
            node.addProperty("value", (int)field.get(null));
        } else if (type.equals(Long.TYPE)) {
            node.addProperty("value", (long)field.get(null));
        } else if (type.isEnum()) {
            try {
                Method toString = type.getMethod("toString");
                node.addProperty("value", (String)toString.invoke(field.get(null)));
            } catch(NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return node;
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
            "value" : 42
         },
         ...
    }

    comments, name fields will be ignored when loading the config, but it will make config editing way easier.
     */
}
