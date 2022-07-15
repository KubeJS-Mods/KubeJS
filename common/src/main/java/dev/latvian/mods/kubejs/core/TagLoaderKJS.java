package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.item.ingredient.TagIngredientJS;
import dev.latvian.mods.kubejs.server.TagEventJS;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagLoader;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * @author LatvianModder
 */
public interface TagLoaderKJS<T> {
	default void customTagsKJS(Map<ResourceLocation, List<TagLoader.EntryWithSource>> map) {
		TagIngredientJS.resetContext();
		var dir = getDirectory();
		var reg = getRegistryKJS();

		if (reg != null) {
			new TagEventJS<>(dir, map, reg).post();
		}
	}

	void setRegistryKJS(Registry<T> registry);

	@Nullable
	Registry<T> getRegistryKJS();

	String getDirectory();
}