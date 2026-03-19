package dev.latvian.mods.kubejs.core.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.latvian.mods.kubejs.core.TagLoaderKJS;
import dev.latvian.mods.kubejs.server.ServerScriptManager;
import dev.latvian.mods.kubejs.util.TagReloadContextKJS;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagLoader;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;

@Mixin(TagLoader.class)
public abstract class TagLoaderMixin<T> implements TagLoaderKJS<T> {
	@Unique
	private @Nullable ServerScriptManager kjs$serverScriptManager;

	@Unique
	private @Nullable Registry<T> kjs$storedRegistry;

	@Override
	public void kjs$init(ServerScriptManager serverScriptManager, Registry<T> registry) {
		kjs$serverScriptManager = serverScriptManager;
		kjs$storedRegistry = registry;
	}

	@Inject(
		method = "loadPendingTags(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/core/Registry;)Ljava/util/Optional;",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/tags/TagLoader;load(Lnet/minecraft/server/packs/resources/ResourceManager;)Ljava/util/Map;",
			shift = At.Shift.BEFORE
		)
	)
	private static <T> void kjs$initLoaderBeforeLoad(
		ResourceManager manager,
		Registry<T> registry,
		CallbackInfoReturnable<?> cir,
		@Local(name = "loader") TagLoader<Holder<T>> loader
	) {
		ServerScriptManager ssm = TagReloadContextKJS.CURRENT.get();
		if (ssm != null) {
			((TagLoaderKJS<T>) loader).kjs$init(ssm, registry);
		}
	}

	@Redirect(
		method = "loadPendingTags(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/core/Registry;)Ljava/util/Optional;",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/tags/TagLoader;load(Lnet/minecraft/server/packs/resources/ResourceManager;)Ljava/util/Map;"
		)
	)
	private static <T> Map<Identifier, List<TagLoader.EntryWithSource>> kjs$customTagsBeforeBuild(
		TagLoader<Holder<T>> loader,
		ResourceManager manager
	) {
		Map<Identifier, List<TagLoader.EntryWithSource>> map = loader.load(manager);
		ServerScriptManager ssm = TagReloadContextKJS.CURRENT.get();
		if (ssm != null) {
			((TagLoaderKJS<T>) loader).kjs$customTags(ssm, map);
		}
		return map;
	}

	@Override
	@Nullable
	public ServerScriptManager kjs$getServerScriptManager() {
		return kjs$serverScriptManager;
	}

	@Override
	public @Nullable Registry<T> kjs$getRegistry() {
		return kjs$storedRegistry;
	}
}