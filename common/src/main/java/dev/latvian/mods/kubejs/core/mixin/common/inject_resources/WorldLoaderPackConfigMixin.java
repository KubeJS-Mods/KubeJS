package dev.latvian.mods.kubejs.core.mixin.common.inject_resources;

import dev.latvian.mods.kubejs.server.ServerScriptManager;
import net.minecraft.server.WorldLoader;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(WorldLoader.PackConfig.class)
public abstract class WorldLoaderPackConfigMixin {

	@Redirect(method = "createResourceManager", at = @At(
			value = "NEW",
			target = "net/minecraft/server/packs/resources/MultiPackResourceManager"
	))
	private static MultiPackResourceManager injectKubeJSPacks(PackType packType, List<PackResources> list) {
		ServerScriptManager.instance = new ServerScriptManager();
		return ServerScriptManager.instance.wrapResourceManager(packType, list);
	}
}
