package io.github.blanketmc.blanket;

import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Properties;
import java.util.Set;

public class MixinPlugin implements IMixinConfigPlugin {
    private final Properties mixinConfig = new Properties();

    @Override
    public void onLoad(String mixinPackage) {
        var configPath = FabricLoader.getInstance().getConfigDir().resolve("blanket-client.mixin.properties");
        try (var file = Files.newInputStream(configPath)) {
            mixinConfig.load(file);
        } catch (IOException e) {
            // oops
        }
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        var id = mixinClassName.substring(mixinClassName.lastIndexOf(".") + 1);
        if (mixinConfig.getProperty(id) != null) {
            return Boolean.parseBoolean(mixinConfig.getProperty(id));
        } else return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}
