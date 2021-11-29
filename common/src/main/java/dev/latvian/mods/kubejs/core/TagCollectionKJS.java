package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.item.ingredient.TagIngredientJS;
import dev.latvian.mods.kubejs.server.TagEventJS;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author LatvianModder
 */
public interface TagCollectionKJS<T> {
	default void customTagsKJS(Map<ResourceLocation, Tag.Builder> map) {
		TagIngredientJS.clearTagCache();
		String c = getResourceLocationPrefixKJS().substring(5);
		new TagEventJS<T>(c, map, getRegistryKJS()).post("tags." + c);
	}

	Function<ResourceLocation, Optional<T>> getRegistryKJS();

	String getResourceLocationPrefixKJS();
}