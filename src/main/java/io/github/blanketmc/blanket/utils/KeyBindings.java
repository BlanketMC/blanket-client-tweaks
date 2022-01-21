package io.github.blanketmc.blanket.utils;

import io.github.blanketmc.blanket.Config;
import io.github.blanketmc.blanket.config.ConfigEntry;
import io.github.blanketmc.blanket.config.ConfigHelper;
import io.github.blanketmc.blanket.config.EntryListener;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.Field;

import static io.github.blanketmc.blanket.ClientFixes.config;

public class KeyBindings {
    private static final KeyBinding rotatePlayerWithMinecraft_toggle = KeyBindingHelper.registerKeyBinding(
            new KeyBinding(
                    "blanket-client-tweaks.keys.rotateMinecartKey",
                    InputUtil.Type.KEYSYM,
                    GLFW.GLFW_KEY_UNKNOWN,
                    "blanket-client-tweaks.keys.category"
            )
    );

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(KeyBindings::eventListener);
    }

    private static void eventListener(MinecraftClient minecraftClient) {
        while (rotatePlayerWithMinecraft_toggle.wasPressed()) {
            boolean oldVal = Config.rotatePlayerWithMinecart;
            Config.rotatePlayerWithMinecart = !Config.rotatePlayerWithMinecart;


            try {
                Field field = Config.class.getField("rotatePlayerWithMinecart");
                if (field.getAnnotation(ConfigEntry.class).listeners().length > 0) {
                    boolean currentValue = field.getBoolean(config);
                    for (Class<? extends EntryListener<Boolean>> listener : field.getAnnotation(ConfigEntry.class).listeners()) {
                        Config.rotatePlayerWithMinecart = (ConfigHelper.callClassConstructor(listener)).onEntryChange(currentValue, oldVal);
                    }
                }
            } catch(NoSuchFieldException | IllegalAccessException ignore) {}
        }
    }
}
