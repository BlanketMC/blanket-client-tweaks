package io.github.blanketmc.blanket.utils.platforms;

import java.net.URL;

public interface IPlatform {
    boolean isModLoaded(String modID);


    URL getUrl(ClassLoader loader, String file);

}
