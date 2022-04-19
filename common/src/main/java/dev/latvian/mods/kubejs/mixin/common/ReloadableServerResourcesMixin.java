package dev.latvian.mods.kubejs.mixin.common;

import dev.latvian.mods.kubejs.server.ServerScriptManager;
import net.minecraft.commands.Commands;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author LatvianModder
 */
@Mixin(ReloadableServerResources.class)
public abstract class ReloadableServerResourcesMixin {
	@Inject(method = "<init>", at = @At("RETURN"))
	private void init(RegistryAccess.Frozen frozen, Commands.CommandSelection commandSelection, int i, CallbackInfo ci) {
		ServerScriptManager.instance = new ServerScriptManager();
		ServerScriptManager.instance.init((ReloadableServerResources) (Object) this);
	}

	@ModifyArg(method = "loadResources", at = @At(value = "INVOKE", ordinal = 0,
			target = "Lnet/minecraft/server/packs/resources/SimpleReloadInstance;create(Lnet/minecraft/server/packs/resources/ResourceManager;Ljava/util/List;Ljava/util/concurrent/Executor;Ljava/util/concurrent/Executor;Ljava/util/concurrent/CompletableFuture;Z)Lnet/minecraft/server/packs/resources/ReloadInstance;"),
			index = 0)
	private static ResourceManager wrapResourceManager(ResourceManager rm) {
		return ServerScriptManager.instance.wrapResourceManager(rm);
	}
}
