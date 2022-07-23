package dev.latvian.mods.kubejs.mixin.common.inject_resources;

import dev.latvian.mods.kubejs.server.ServerScriptManager;
import net.minecraft.server.WorldStem;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(WorldStem.class)
public abstract class WorldStemMixin {

	@ModifyVariable(method = "load", at = @At("STORE"))
	private static CloseableResourceManager injectKubeJSPacks(CloseableResourceManager original) {
		ServerScriptManager.instance = new ServerScriptManager();
		return ServerScriptManager.instance.wrapResourceManager(original);
	}
}
