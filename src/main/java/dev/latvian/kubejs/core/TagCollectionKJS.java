package dev.latvian.kubejs.core;

import dev.latvian.kubejs.item.ingredient.TagIngredientJS;
import dev.latvian.kubejs.server.TagEventJS;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author LatvianModder
 */
public interface TagCollectionKJS<T>
{
	default void customTagsKJS(Map<ResourceLocation, ITag.Builder> map)
	{
		TagIngredientJS.clearTagCache();
		String c = getResourceLocationPrefixKJS().substring(5);
		String t = getItemTypeNameKJS();
		new TagEventJS<T>(c, map, getRegistryKJS()).post(t + ".tags");
	}

	Function<ResourceLocation, Optional<T>> getRegistryKJS();

	String getResourceLocationPrefixKJS();

	String getItemTypeNameKJS();
}