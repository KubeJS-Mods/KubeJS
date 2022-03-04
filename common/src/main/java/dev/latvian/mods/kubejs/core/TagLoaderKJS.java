package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.item.ingredient.TagIngredientJS;
import dev.latvian.mods.kubejs.server.TagEventJS;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * @author LatvianModder
 */
public interface TagLoaderKJS<T> {
	default void customTagsKJS(Map<ResourceLocation, Tag.Builder> map) {
		TagIngredientJS.clearTagCache();
		var c = getDirectory().substring(5);
		var reg = getRegistryKJS();
		if (reg != null) {
			new TagEventJS<>(c, map, reg).post("tags." + c.replaceAll("([/:])", "."));
		}
	}

	void setRegistryKJS(Registry<T> registry);

	@Nullable
	Registry<T> getRegistryKJS();

	String getDirectory();
}