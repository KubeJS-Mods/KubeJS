package dev.latvian.mods.kubejs.mixin.common;

import dev.latvian.mods.kubejs.core.TagLoaderKJS;
import dev.latvian.mods.kubejs.server.ServerScriptManager;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagLoader;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;

/**
 * @author LatvianModder
 */
@Mixin(TagLoader.class)
public abstract class TagLoaderMixin<T> implements TagLoaderKJS<T> {

	@Nullable
	@Unique
	private Registry<T> kjs$storedRegistry;

	@Inject(method = "load", at = @At("RETURN"))
	private void customTags(ResourceManager resourceManager, CallbackInfoReturnable<Map<ResourceLocation, List<TagLoader.EntryWithSource>>> cir) {
		// band-aid fix for #237, as some mods use tags on the client side;
		// technically not an intended use case, but easy enough to fix
		if (ServerScriptManager.instance != null) {
			kjs$customTags(cir.getReturnValue());
		}
	}

	@Override
	public void kjs$setRegistry(Registry<T> registry) {
		kjs$storedRegistry = registry;
	}

	@Override
	public @Nullable Registry<T> kjs$getRegistry() {
		return kjs$storedRegistry;
	}

	@Override
	@Accessor("directory")
	public abstract String kjs$getDirectory();
}