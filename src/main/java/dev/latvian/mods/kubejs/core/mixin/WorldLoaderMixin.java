package dev.latvian.mods.kubejs.core.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.latvian.mods.kubejs.core.ReloadableServerResourcesKJS;
import dev.latvian.mods.kubejs.util.TagReloadContextKJS;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.WorldLoader;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(WorldLoader.class)
public class WorldLoaderMixin {
	@Inject(
		method = "load",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/resources/RegistryDataLoader;load(Lnet/minecraft/server/packs/resources/ResourceManager;Ljava/util/List;Ljava/util/List;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;",
			shift = At.Shift.BEFORE
		)
	)
	private static <D, R> void kjs$load(
		WorldLoader.InitConfig initConfig,
		WorldLoader.WorldDataSupplier<D> worldDataSupplier,
		WorldLoader.ResultFactory<D, R> resultFactory,
		Executor backgroundExecutor,
		Executor gameExecutor,
		CallbackInfoReturnable<CompletableFuture<R>> cir,
		@Local RegistryAccess.Frozen registriesWithDimensions
	) {
		RegistryAccessContainer.current = new RegistryAccessContainer(registriesWithDimensions);
	}

	@Inject(method = "load", at = @At(value = "INVOKE", target = "Lcom/mojang/datafixers/util/Pair;getFirst()Ljava/lang/Object;", shift = At.Shift.BEFORE))
	private static <D, R> void kjs$load2(
		WorldLoader.InitConfig initConfig,
		WorldLoader.WorldDataSupplier<D> worldDataSupplier,
		WorldLoader.ResultFactory<D, R> resultFactory,
		Executor backgroundExecutor,
		Executor gameExecutor,
		CallbackInfoReturnable<CompletableFuture<R>> cir,
		@Local(ordinal = 1) RegistryAccess.Frozen registriesWithEverything
	) {
		RegistryAccessContainer.current = new RegistryAccessContainer(registriesWithEverything);
	}

	@WrapOperation(
		method = "load",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/tags/TagLoader;loadTagsForExistingRegistries(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/core/RegistryAccess;)Ljava/util/List;"
		)
	)
	private static List<Registry.PendingTags<?>> kjs$withTagContext_worldLoader(
		ResourceManager manager,
		RegistryAccess layer,
		Operation<List<Registry.PendingTags<?>>> original
	) {
		ReloadableServerResourcesKJS prev = TagReloadContextKJS.CURRENT.get();
		if (prev == null) {
			TagReloadContextKJS.CURRENT.remove();
		} else {
			TagReloadContextKJS.CURRENT.set(prev);
		}

		try {
			TagReloadContextKJS.CURRENT.remove();
			return original.call(manager, layer);
		} finally {
			if (prev == null) {
				TagReloadContextKJS.CURRENT.remove();
			} else {
				TagReloadContextKJS.CURRENT.set(prev);
			}
		}
	}

}
