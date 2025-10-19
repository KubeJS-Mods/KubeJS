package dev.latvian.mods.kubejs.core.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.WorldLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(WorldLoader.class)
public class WorldLoaderMixin {
	@Inject(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/resources/RegistryDataLoader;load(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/core/RegistryAccess;Ljava/util/List;)Lnet/minecraft/core/RegistryAccess$Frozen;", shift = At.Shift.BEFORE))
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
}
