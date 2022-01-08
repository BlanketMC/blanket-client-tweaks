package io.github.blanketmc.blanket.config;

import com.google.gson.*;
import io.github.blanketmc.blanket.Config;

import java.lang.reflect.Type;

public class ConfigJsonSerializer implements JsonSerializer<Config>, JsonDeserializer<Config> {
    @Override
    public Config deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return null;
    }

    @Override
    public JsonElement serialize(Config src, Type typeOfSrc, JsonSerializationContext context) {
        return null;
    }

    /*
    it will be:

    {
        "option 1" : {
            "name" : "The name of the first option",
            "description" : "some description",
            "Categories" : "GENERAL, BUGFIX, RECOMMENDED", //as literal text :D
            "value" : 42
         },
         ...
    }

    comments, name fields will be ignored when loading the config, but it will make config editing way easier.
     */
}
