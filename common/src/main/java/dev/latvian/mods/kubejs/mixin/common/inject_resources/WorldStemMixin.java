package dev.latvian.mods.kubejs.mixin.common.inject_resources;

import dev.latvian.mods.kubejs.server.ServerScriptManager;
import net.minecraft.server.WorldStem;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(WorldStem.class)
public abstract class WorldStemMixin {

	@Inject(method = "load", at = @At("HEAD"))
	private static void init(WorldStem.InitConfig initConfig, WorldStem.DataPackConfigSupplier packConfig, WorldStem.WorldDataSupplier dataSupplier,
							 Executor bg, Executor fg, CallbackInfoReturnable<CompletableFuture<WorldStem>> cir) {
		// **way** too early to do anything with this, but we need it to be initialised before wrapping the resource manager
		ServerScriptManager.instance = new ServerScriptManager();
	}


	@Redirect(method = "load", at = @At(
			value = "NEW",
			target = "net/minecraft/server/packs/resources/MultiPackResourceManager"
	))
	private static MultiPackResourceManager injectKubeJSPacks(PackType packType, List<PackResources> list) {
		return ServerScriptManager.instance.wrapResourceManager(packType, list);
	}
}
