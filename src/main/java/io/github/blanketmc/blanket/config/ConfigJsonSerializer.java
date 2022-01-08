package io.github.blanketmc.blanket.config;

import com.google.gson.*;
import io.github.blanketmc.blanket.Config;
import io.github.blanketmc.blanket.FabricClientModInitializer;
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
        var config = new Config();
        ConfigHelper.iterateOnConfig((field, configEntry) -> {

            String entryName = configEntry.name().equals("") ? field.getName() : configEntry.name();
            if (node.has(entryName)) {

                var configNode = node.get(entryName).getAsJsonObject();
                if (!configNode.has("value")) return;


                var type = field.getType();

                if (type.equals(Boolean.TYPE)) {
                    if (!configNode.get("value").getAsJsonPrimitive().isBoolean()) return;
                    field.set(config, configNode.get("value").getAsBoolean());
                }
                else if (type.equals(Float.TYPE)) {
                    if (!configNode.get("value").getAsJsonPrimitive().isNumber()) return;
                    field.set(config, configNode.get("value").getAsFloat());
                }
                else if (type.isEnum()) {
                    if (!configNode.get("value").getAsJsonPrimitive().isString()) return;
                    //now, this is tricky
                    try {
                        Method valueOf = type.getMethod("valueOf", String.class);
                        field.set(config, valueOf.invoke(null, configNode.get("value").getAsString()));
                    } catch(NoSuchMethodException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                } else {
                    FabricClientModInitializer.log(Level.ERROR, "Config: " + field.getName() + " can not be imported: Unknown type", true);
                }
            }
        });
        return config;
    }

    @Override
    public JsonElement serialize(Config src, Type typeOfSrc, JsonSerializationContext context) {
        var node = new JsonObject();

        ConfigHelper.iterateOnConfig((field, configEntry) -> {
            node.add(configEntry.name().equals("") ? field.getName() : configEntry.name(), writeField(src, field, configEntry));
        });
        return node;
    }

    public JsonElement writeField(Config config, Field field, ConfigEntry configEntry) throws IllegalAccessException {
        var node = new JsonObject();

        if (!configEntry.displayName().equals("")) {
            node.addProperty("name", configEntry.displayName());
        }

        if (!configEntry.description().equals("")) {
            node.addProperty("description", configEntry.description());
        }

        var categories = new JsonArray();
        for (ConfigEntry.Category category : configEntry.categories()) {
            categories.add(category.toString());
        }
        node.add("categories", categories);


        Class<?> type = field.getType();

        if (type.equals(Boolean.TYPE)) {
            node.addProperty("value", (boolean)field.get(config));
        }
        if (type.equals(Float.TYPE)) {
            node.addProperty("value", (float)field.get(config));
        }
        if (type.isEnum()) {
            try {
                Method toString = type.getMethod("toString");
                node.addProperty("value", (String)toString.invoke(field.get(config)));
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
            "categories" : "GENERAL, BUGFIX, RECOMMENDED", //as literal text :D
            "value" : 42
         },
         ...
    }

    comments, name fields will be ignored when loading the config, but it will make config editing way easier.
     */
}
