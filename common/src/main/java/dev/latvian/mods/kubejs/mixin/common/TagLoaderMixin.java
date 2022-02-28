package dev.latvian.mods.kubejs.mixin.common;

import dev.latvian.mods.kubejs.core.TagLoaderKJS;
import dev.latvian.mods.kubejs.server.ServerScriptManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author LatvianModder
 */
// TODO: we might consider moving this to inject into TagManager
//  for the extra context provided by that
@Mixin(TagLoader.class)
public abstract class TagLoaderMixin<T> implements TagLoaderKJS<T> {
	@Inject(method = "load", at = @At("RETURN"))
	private void customTags(ResourceManager resourceManager, CallbackInfoReturnable<Map<ResourceLocation, Tag.Builder>> cir) {
		// band-aid fix for #237, as some mods use tags on the client side;
		// technically not an intended use case, but easy enough to fix
		if(ServerScriptManager.instance != null) {
			customTagsKJS(cir.getReturnValue());
		}
	}

	@Override
	@Accessor("idToValue")
	public abstract Function<ResourceLocation, Optional<T>> getRegistryKJS();

	@Override
	@Accessor("directory")
	public abstract String getResourceLocationPrefixKJS();
}