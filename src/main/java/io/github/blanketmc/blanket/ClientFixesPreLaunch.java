package io.github.blanketmc.blanket;

import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import net.fabricmc.loader.impl.util.UrlConversionException;
import net.fabricmc.loader.impl.util.UrlUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;

public class ClientFixesPreLaunch implements PreLaunchEntrypoint {
    // Allow the mod to mixin directly into library classes

    private static final String[] mixinTargets = {
            "com/mojang/authlib/yggdrasil/YggdrasilUserApiService.class"
    };

    private static URL getUrl(ClassLoader loader, String file) {
        URL url = loader.getResource(file);
        if (url == null) return null;
        try {
            if ((url = UrlUtil.getSource(file, url)) != null) return url;
        } catch (UrlConversionException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onPreLaunch() {
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            Method method = classLoader.getClass().getMethod("addURL", URL.class);
            method.setAccessible(true);
            for (String mixinTarget : mixinTargets) {
                URL url = getUrl(classLoader.getParent().getParent().getParent(), mixinTarget);
                if (url == null) {
                    System.out.println("Unable to apply: "+ mixinTarget);
                    continue;
                }
                method.invoke(classLoader, url);
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
