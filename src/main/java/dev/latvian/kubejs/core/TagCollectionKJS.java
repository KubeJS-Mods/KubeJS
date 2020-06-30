package dev.latvian.kubejs.core;

import dev.latvian.kubejs.server.TagEventJS;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

import java.util.Map;

/**
 * @author LatvianModder
 */
public interface TagCollectionKJS
{
	default <T> void customTagsKJS(Map<ResourceLocation, ITag.Builder> map)
	{
		if (this instanceof NetworkTagCollectionKJS)
		{
			String c = getResourceLocationPrefixKJS().substring(5);
			String t = getItemTypeNameKJS();
			Registry<T> r = ((NetworkTagCollectionKJS) this).getRegistryKJS();
			new TagEventJS<T>(c, map, r).post(t + ".tags");
		}
	}

	String getResourceLocationPrefixKJS();

	String getItemTypeNameKJS();
}