package dev.latvian.mods.kubejs.core.mixin;

import com.mojang.datafixers.util.Pair;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.WorldLoader;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import net.minecraft.world.level.WorldDataConfiguration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(WorldLoader.class)
public class WorldLoaderMixin {
	@Inject(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/resources/RegistryDataLoader;load(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/core/RegistryAccess;Ljava/util/List;)Lnet/minecraft/core/RegistryAccess$Frozen;", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD)
	private static <D, R> void kjs$load(
		WorldLoader.InitConfig initConfig,
		WorldLoader.WorldDataSupplier<D> worldDataSupplier,
		WorldLoader.ResultFactory<D, R> resultFactory,
		Executor backgroundExecutor,
		Executor gameExecutor,
		CallbackInfoReturnable<CompletableFuture<R>> cir,
		Pair<WorldDataConfiguration, CloseableResourceManager> config,
		CloseableResourceManager resourceManager,
		LayeredRegistryAccess<RegistryLayer> builtinRegistries,
		LayeredRegistryAccess<RegistryLayer> worldgenRegistries,
		RegistryAccess.Frozen registriesWithDimensions
	) {
		RegistryAccessContainer.current = new RegistryAccessContainer(registriesWithDimensions);
	}

	@Inject(method = "load", at = @At(value = "INVOKE", target = "Lcom/mojang/datafixers/util/Pair;getFirst()Ljava/lang/Object;", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD)
	private static <D, R> void kjs$load2(
		WorldLoader.InitConfig initConfig,
		WorldLoader.WorldDataSupplier<D> worldDataSupplier,
		WorldLoader.ResultFactory<D, R> resultFactory,
		Executor backgroundExecutor,
		Executor gameExecutor,
		CallbackInfoReturnable<CompletableFuture<R>> cir,
		Pair<WorldDataConfiguration, CloseableResourceManager> config,
		CloseableResourceManager resourceManager,
		LayeredRegistryAccess<RegistryLayer> builtinRegistries,
		LayeredRegistryAccess<RegistryLayer> worldgenRegistries,
		RegistryAccess.Frozen registriesWithDimensions,
		RegistryAccess.Frozen registriesWithEverything
	) {
		RegistryAccessContainer.current = new RegistryAccessContainer(registriesWithEverything);
	}
}
