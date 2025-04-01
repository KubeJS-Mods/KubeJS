package dev.latvian.mods.kubejs;

import net.neoforged.fml.loading.LoadingModList;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class KubeJSMixinPlugin implements IMixinConfigPlugin {
    @Override
    public void onLoad(String mixinPackage) {

    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(@NotNull String targetClassName, @NotNull String mixinClassName) {
        if (targetClassName.contains("mezz/modnametooltip")) {
            return LoadingModList.get().getModFileById("modnametooltip") != null;
        }

        if (targetClassName.contains("me/shedaniel/rei")) {
            return LoadingModList.get().getModFileById("roughlyenoughitems") != null;
        }

        return true;
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