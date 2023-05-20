package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.bindings.event.ServerEvents;
import dev.latvian.mods.kubejs.item.ingredient.TagContext;
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
	default void kjs$customTags(Map<ResourceLocation, List<TagLoader.EntryWithSource>> map) {
		TagContext.INSTANCE.setValue(TagContext.EMPTY);
		if (ServerEvents.TAGS.hasListeners()) {
			var dir = kjs$getDirectory();
			var reg = kjs$getRegistry();

			if (reg != null) {
				new TagEventJS<>(dir, map, reg).post();
			}
		}
	}

	void kjs$setRegistry(Registry<T> registry);

	@Nullable
	Registry<T> kjs$getRegistry();

	String kjs$getDirectory();
}