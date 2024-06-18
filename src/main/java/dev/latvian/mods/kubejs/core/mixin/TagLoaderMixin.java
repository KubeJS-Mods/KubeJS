package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.core.ReloadableServerResourcesKJS;
import dev.latvian.mods.kubejs.core.TagLoaderKJS;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagLoader;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Mixin(TagLoader.class)
public abstract class TagLoaderMixin<T> implements TagLoaderKJS<T> {
	@Unique
	private ReloadableServerResourcesKJS kjs$resources;

	@Nullable
	@Unique
	private Registry<T> kjs$storedRegistry;

	@Inject(method = "load", at = @At("RETURN"))
	private void customTags(ResourceManager resourceManager, CallbackInfoReturnable<Map<ResourceLocation, List<TagLoader.EntryWithSource>>> cir) {
		kjs$customTags(kjs$resources, cir.getReturnValue());
	}

	@Override
	public void kjs$init(ReloadableServerResourcesKJS resources, Registry<T> registry) {
		kjs$resources = resources;
		kjs$storedRegistry = registry;
	}

	@Override
	public ReloadableServerResourcesKJS kjs$getResources() {
		return kjs$resources;
	}

	@Override
	@Nullable
	public Registry<T> kjs$getRegistry() {
		return kjs$storedRegistry;
	}

	@Override
	@Invoker("build")
	public abstract Map<ResourceLocation, Collection<T>> kjs$callBuild(Map<ResourceLocation, List<TagLoader.EntryWithSource>> map);
}