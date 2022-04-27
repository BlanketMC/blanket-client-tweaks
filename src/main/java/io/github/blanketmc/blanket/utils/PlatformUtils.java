package io.github.blanketmc.blanket.utils;

import io.github.blanketmc.blanket.utils.platforms.FabricPlatform;
import io.github.blanketmc.blanket.utils.platforms.IPlatform;
import io.github.blanketmc.blanket.utils.platforms.QuiltPlatform;

public class PlatformUtils {

    private static IPlatform INSTANCE = null;

    public static IPlatform getInstance() {
        if (INSTANCE == null) {
            if (isUsingQuilt()) {
                INSTANCE = new QuiltPlatform();
            } else {
                INSTANCE = new FabricPlatform();
            }
        }
        return INSTANCE;
    }

    private static boolean isUsingQuilt() {
        try {
            var clazz = Class.forName("org.quiltmc.loader.api.QuiltLoader");
            return true;
        } catch(ClassNotFoundException ignore) {
            return false;
        }
    }

}
