package dev.latvian.mods.kubejs.mixin.common;

import dev.latvian.mods.kubejs.server.KubeJSReloadListener;
import dev.latvian.mods.kubejs.server.ServerScriptManager;
import net.minecraft.commands.Commands;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.ServerResources;
import net.minecraft.server.packs.PackResources;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/**
 * @author LatvianModder
 */
@Mixin(ServerResources.class)
public abstract class ServerResourcesMixin {
	@Inject(method = "<init>", at = @At("RETURN"))
	private void init(RegistryAccess registryAccess, Commands.CommandSelection commandSelection, int i, CallbackInfo ci) {
		ServerScriptManager.instance = new ServerScriptManager();
		ServerScriptManager.instance.init((ServerResources) (Object) this);
		KubeJSReloadListener.resources = (ServerResources) (Object) this;
	}

	@ModifyArg(method = "loadResources", at = @At(value = "INVOKE", ordinal = 0,
			target = "Lnet/minecraft/server/packs/resources/ReloadableResourceManager;reload(Ljava/util/concurrent/Executor;Ljava/util/concurrent/Executor;Ljava/util/List;Ljava/util/concurrent/CompletableFuture;)Ljava/util/concurrent/CompletableFuture;"),
			index = 2)
	private static List<PackResources> resourcePackList(List<PackResources> list) {
		return ServerScriptManager.instance.resourcePackList(list);
	}
}
