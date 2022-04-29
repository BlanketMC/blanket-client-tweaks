package io.github.blanketmc.blanket.utils.platforms;

import org.quiltmc.loader.api.QuiltLoader;
import org.quiltmc.loader.impl.util.UrlConversionException;
import org.quiltmc.loader.impl.util.UrlUtil;

import java.net.URL;

public class QuiltPlatform implements IPlatform {

    @Override
    public boolean isModLoaded(String modID) {
        return QuiltLoader.isModLoaded(modID);
    }

    @Override
    public URL getUrl(ClassLoader loader, String file) {
        URL url = loader.getResource(file);
        if (url == null) return null;
        try {
            if ((url = UrlUtil.getSource(file, url)) != null) return url;
        } catch(UrlConversionException e) {
            e.printStackTrace();
        }
        return null;
    }


}