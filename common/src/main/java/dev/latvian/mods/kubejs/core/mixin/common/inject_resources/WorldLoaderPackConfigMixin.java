package dev.latvian.mods.kubejs.core.mixin.common.inject_resources;

import dev.latvian.mods.kubejs.server.ServerScriptManager;
import net.minecraft.server.WorldLoader;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(WorldLoader.PackConfig.class)
public abstract class WorldLoaderPackConfigMixin {

	@ModifyVariable(method = "createResourceManager", at = @At("STORE"))
	private CloseableResourceManager injectKubeJSPacks(CloseableResourceManager original) {
		ServerScriptManager.instance = new ServerScriptManager();
		return ServerScriptManager.instance.wrapResourceManager(original);
	}
}
