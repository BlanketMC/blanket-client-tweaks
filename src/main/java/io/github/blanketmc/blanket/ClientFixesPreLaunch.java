package io.github.blanketmc.blanket;

import io.github.blanketmc.blanket.utils.PlatformUtils;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import org.spongepowered.asm.mixin.Mixins;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;

public class ClientFixesPreLaunch implements PreLaunchEntrypoint {
    // Allow the mod to mixin directly into library classes

    /**
     * load mixins with conditions
     */
    private static void loadMixinsSelectively() {
        if (PlatformUtils.getInstance().isModLoaded("sodium-extra")) {
            Mixins.addConfiguration("blanket-client-tweaks_no-sodium-extra.json");
        }
    }


    private static final String[] mixinTargets = {
            "com/mojang/authlib/yggdrasil/YggdrasilUserApiService.class"
    };

    @Override
    public void onPreLaunch() {
        loadMixinsSelectively();
        /*
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            Method method = classLoader.getClass().getMethod("addURL", URL.class);
            method.setAccessible(true);
            for (String mixinTarget : mixinTargets) {
                URL url = PlatformUtils.getInstance().getUrl(classLoader.getParent().getParent().getParent(), mixinTarget);
                if (url == null) {
                    System.out.println("Unable to apply: "+ mixinTarget);
                    continue;
                }
                method.invoke(classLoader, url);
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

         */
    }
}
