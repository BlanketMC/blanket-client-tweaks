package io.github.blanketmc.blanket.utils;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.github.blanketmc.blanket.ClientFixes;
import io.github.blanketmc.blanket.config.ConfigEntry;
import io.github.blanketmc.blanket.config.ConfigHelper;
import io.github.blanketmc.blanket.config.EntryListener;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static io.github.blanketmc.blanket.ClientFixes.config;

@SuppressWarnings({"rawtypes", "unchecked"})
public class ClientCommands {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public static final String PREFIX = "client-fixes";


    public static void registerCommands(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        LiteralArgumentBuilder<FabricClientCommandSource> builder = ClientCommandManager.literal(PREFIX);
        builder.executes((c) -> {sendToPlayer("/"+PREFIX+" [list/<entry>]");return 1;});
        ConfigHelper.iterateOnConfig((field, configEntry) -> {
            builder.then(ClientCommandManager.literal(field.getName())
                            .then(ClientCommandManager.argument("value", StringArgumentType.word()).executes((c) -> modifyEntry(c,field, configEntry)))
                    .executes((c) -> getEntryInfo(field, configEntry)));
        });
        builder.then(ClientCommandManager.literal("list")
                .then(ClientCommandManager.argument("category",new EnumArgumentType<>(ConfigEntry.Category.class)).executes(ClientCommands::listCategoryEntries))
                .executes(ClientCommands::listCategories)
        );
        dispatcher.register(builder);
    }

    private static int listCategories(CommandContext<FabricClientCommandSource> context) {
        MutableText description = Text.literal("\n§f§lCategories:\n");
        for (ConfigEntry.Category category : ConfigEntry.Category.values()) {
            description.append(Text.literal(category.name().toLowerCase()+"\n").styled((style) -> {
                return style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + PREFIX + " list " + category.name()));
            }));
        }
        sendToPlayer(description);
        return 1;
    }

    private static int listCategoryEntries(CommandContext<FabricClientCommandSource> context) {
        ConfigEntry.Category category = context.getArgument("category", ConfigEntry.Category.class);
        List<Field> entries = ConfigHelper.getConfigFieldsForCategory(category);
        MutableText description = Text.literal("\n§f§l"+category.name()+":\n");
        for (Field field : entries) {
            description.append(Text.literal(field.getName()+"\n").styled((style) -> {
                return style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + PREFIX + " " + field.getName()));
            }));
        }
        sendToPlayer(description);
        return 1;
    }

    private static int modifyEntry(CommandContext<FabricClientCommandSource> context, Field field, ConfigEntry configEntry) {
        try {
            String value = StringArgumentType.getString(context, "value");
            if (field.getType().equals(Boolean.TYPE)) {
                boolean aBoolean = Boolean.parseBoolean(value);
                if (configEntry.listeners().length > 0) {
                    Boolean currentValue = field.getBoolean(config);
                    for (Class<? extends EntryListener> listener : configEntry.listeners()) {
                        aBoolean = (Boolean) (ConfigHelper.callClassConstructor(listener)).onEntryChange(currentValue, aBoolean);
                    }
                }
                field.setBoolean(config, aBoolean);
            } else if (field.getType().equals(Integer.TYPE)) {
                int aInt = Integer.parseInt(value);
                if (configEntry.listeners().length > 0) {
                    Integer currentValue = field.getInt(config);
                    for (Class<? extends EntryListener> listener : configEntry.listeners()) {
                        aInt = (Integer) (ConfigHelper.callClassConstructor(listener)).onEntryChange(currentValue, aInt);
                    }
                }
                field.setInt(config, aInt);
            } else if (field.getType().equals(Double.TYPE)) {
                double aDouble = Double.parseDouble(value);
                if (configEntry.listeners().length > 0) {
                    Double currentValue = field.getDouble(config);
                    for (Class<? extends EntryListener> listener : configEntry.listeners()) {
                        aDouble = (Double) (ConfigHelper.callClassConstructor(listener)).onEntryChange(currentValue, aDouble);
                    }
                }
                field.setDouble(config, aDouble);
            } else if (field.getType().equals(Float.TYPE)) {
                float aFloat = Float.parseFloat(value);
                if (configEntry.listeners().length > 0) {
                    Float currentValue = field.getFloat(config);
                    for (Class<? extends EntryListener> listener : configEntry.listeners()) {
                        aFloat = (Float) (ConfigHelper.callClassConstructor(listener)).onEntryChange(currentValue, aFloat);
                    }
                }
                field.setFloat(config, aFloat);
            } else if (field.getType().equals(Long.class)) {
                Long aLong = Long.parseLong(value);
                if (configEntry.listeners().length > 0) {
                    Long currentValue = field.getLong(config);
                    for (Class<? extends EntryListener> listener : configEntry.listeners()) {
                        aLong = (Long) (ConfigHelper.callClassConstructor(listener)).onEntryChange(currentValue, aLong);
                    }
                }
                field.setLong(config, aLong);
            } else if (field.getType().equals(String.class)) {
                if (configEntry.listeners().length > 0) {
                    String currentValue = (String) field.get(config);
                    for (Class<? extends EntryListener> listener : configEntry.listeners()) {
                        value = (String) (ConfigHelper.callClassConstructor(listener)).onEntryChange(currentValue, value);
                    }
                }
                field.set(config, value);
            } else if (field.getType().isAssignableFrom(String.class)) {
                Object aObject = field.getType().cast(value);
                if (configEntry.listeners().length > 0) {
                    Object currentValue = field.get(config);
                    for (Class<? extends EntryListener> listener : configEntry.listeners()) {
                        aObject = (ConfigHelper.callClassConstructor(listener)).onEntryChange(currentValue, aObject);
                    }
                }
                field.set(config, aObject);
            } else {
                sendToPlayer("Well then, that type does not have a conversion yet. Nice");
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return 1;
    }

    private static int getEntryInfo(Field field, ConfigEntry configEntry) {
        try {
            sendToPlayer(fancyCommandInfo(field, configEntry));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return 1;
    }

    public static void sendToPlayer(String str) {
        if (mc.player != null) mc.player.sendMessage(Text.literal(str), false);
    }

    public static void sendToPlayer(Text text) {
        if (mc.player != null) mc.player.sendMessage(text, false);
    }

    private static Text fancyCommandInfo(Field field, ConfigEntry entry) throws IllegalAccessException {
        MutableText description = Text.literal("\n§f§l"+field.getName()+"\n");

        if (!entry.description().equals("")) {
            description = description.append(Text.literal("§r"+entry.description()+"\n").formatted(Formatting.RESET));
        }
        if (entry.issues().length > 0) {
            description.append(Text.literal("Fixes: "));
            for (String issue : entry.issues()) {
                description.append(Text.literal("§6["+issue+"]").styled((style) -> {
                    return style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, ClientFixes.mcIssuePrefix+issue));
                }));
            }
            description.append(Text.literal("§r\n"));
        }

        description.append(Text.literal("Tags: "));
        Iterator<ConfigEntry.Category> iterator = Arrays.stream(entry.categories()).iterator();
        while (iterator.hasNext()) {
            ConfigEntry.Category category = iterator.next();
            description.append(Text.literal("§b"+category.name()).styled((style) -> {
                return style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + PREFIX + " list "+category.name()));
            }));
            if (iterator.hasNext()) {
                description.append(Text.literal(","));
            }
        }
        description.append(Text.literal("§r\n"));

        description.append(Text.literal("Current Value: "));
        if (field.getType().equals(Boolean.TYPE)) {
            boolean value = field.getBoolean(config);
            description.append(Text.literal(value ? "§2True" : "§4False"));
        } else {
            boolean isDefault = field.get(config).equals(ConfigHelper.getDefaultValue(field));
            description.append(Text.literal((isDefault ? "§2" : "§b")+field.get(config).toString()));
        }
        return description;
    }

}
