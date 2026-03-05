package dev.latvian.mods.kubejs.core.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.latvian.mods.kubejs.server.ServerScriptManager;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import dev.latvian.mods.kubejs.util.TagReloadContextKJS;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.WorldLoader;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.llamalad7.mixinextras.sugar.Local;

import java.util.List;
import java.util.concurrent.CompletionStage;

@Mixin(WorldLoader.class)
public class WorldLoaderMixin {
	@Inject(
		method = "lambda$load$0",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/resources/RegistryDataLoader;load(Lnet/minecraft/server/packs/resources/ResourceManager;Ljava/util/List;Ljava/util/List;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;",
			shift = At.Shift.BEFORE
		)
	)
	private static void kjs$load(
		CallbackInfoReturnable<CompletionStage<?>> cir,
		@Local RegistryAccess.Frozen worldgenLoadContext
	) {
		RegistryAccessContainer.current = new RegistryAccessContainer(worldgenLoadContext);
	}

	@Inject(
		method = "lambda$load$2",
		at = @At(
			value = "INVOKE",
			target = "Lcom/mojang/datafixers/util/Pair;getFirst()Ljava/lang/Object;",
			shift = At.Shift.BEFORE
		)
	)
	private static void kjs$load2(
		CallbackInfoReturnable<CompletionStage<?>> cir,
		@Local(argsOnly = true, ordinal = 1) RegistryAccess.Frozen initialWorldgenDimensions
	) {
		RegistryAccessContainer.current = new RegistryAccessContainer(initialWorldgenDimensions);
	}

	@WrapOperation(
		method = "lambda$load$0",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/tags/TagLoader;loadTagsForExistingRegistries(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/core/RegistryAccess;)Ljava/util/List;"
		)
	)
	private static List<Registry.PendingTags<?>> kjs$withTagContext(
		ResourceManager manager, RegistryAccess registryAccess, Operation<List<Registry.PendingTags<?>>> original
	) {
		ServerScriptManager prev = TagReloadContextKJS.CURRENT.get();
		TagReloadContextKJS.CURRENT.set(ServerScriptManager.getStaticInstance());
		try {
			return original.call(manager, registryAccess);
		} finally {
			if (prev == null) {
				TagReloadContextKJS.CURRENT.remove();
			} else {
				TagReloadContextKJS.CURRENT.set(prev);
			}
		}
	}
}