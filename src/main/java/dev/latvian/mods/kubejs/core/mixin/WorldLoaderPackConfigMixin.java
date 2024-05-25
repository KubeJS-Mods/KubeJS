package dev.latvian.mods.kubejs.core.mixin;

import net.minecraft.server.WorldLoader;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(WorldLoader.PackConfig.class)
public abstract class WorldLoaderPackConfigMixin {
	//@ModifyVariable(method = "createResourceManager", at = @At("STORE"))
	//private CloseableResourceManager injectKubeJSPacks(CloseableResourceManager original) {
	//	return ServerScriptManager.instance.wrapResourceManager(null, original);
	//}
}
