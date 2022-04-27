package io.github.blanketmc.blanket.utils.platforms;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.impl.util.UrlConversionException;
import net.fabricmc.loader.impl.util.UrlUtil;

import java.net.URL;

public class FabricPlatform implements IPlatform {

    @Override
    public boolean isModLoaded(String modID) {
        return FabricLoader.getInstance().isModLoaded(modID);
    }

    @Override
    public URL getUrl(ClassLoader loader, String file) {
        URL url = loader.getResource(file);
        if (url == null) return null;
        try {
            if ((url = UrlUtil.getSource(file, url)) != null) return url;
        } catch (UrlConversionException e) {
            e.printStackTrace();
        }
        return null;
    }
}
