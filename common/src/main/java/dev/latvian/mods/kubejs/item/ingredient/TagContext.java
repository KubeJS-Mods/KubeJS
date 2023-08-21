package dev.latvian.mods.kubejs.item.ingredient;

import com.google.common.collect.Iterables;
import dev.architectury.extensions.injected.InjectedRegistryEntryExtension;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.TagManager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public interface TagContext {
	TagContext EMPTY = new TagContext() {
		@Override
		public <T> boolean isEmpty(TagKey<T> tag) {
			return true;
		}

		@Override
		public <T> Iterable<Holder<T>> getTag(TagKey<T> tag) {
			KubeJS.LOGGER.warn("Tried to get tag {} from an empty tag context!", tag.location());
			return List.of();
		}
	};

	static TagContext usingRegistry(RegistryAccess registryAccess) {
		return new TagContext() {
			@NotNull
			private <T> Registry<T> registry(TagKey<T> tag) {
				return registryAccess.registryOrThrow(tag.registry());
			}

			@Override
			public boolean areTagsBound() {
				return true;
			}

			@Override
			public <T> Iterable<Holder<T>> getTag(TagKey<T> tag) {
				return registry(tag).getTagOrEmpty(tag);
			}

			@Override
			public <T> boolean contains(TagKey<T> tag, T value) {
				if (value instanceof InjectedRegistryEntryExtension<?> ext) {
					Holder<T> holder = UtilsJS.cast(ext.arch$holder());
					return holder.is(tag);
				}

				// cursed reverse holder lookup using the registry, and fallback to super
				var reg = registry(tag);
				return reg.getResourceKey(value).flatMap(reg::getHolder).map(holder -> holder.is(tag)).orElseGet(() -> TagContext.super.contains(tag, value));
			}
		};
	}

	MutableObject<TagContext> INSTANCE = new MutableObject<>(TagContext.EMPTY);

	static TagContext fromLoadResult(List<TagManager.LoadResult<?>> results) {
		final Map<ResourceKey<? extends Registry<?>>, Map<ResourceLocation, Collection<Holder<?>>>> tags = results.stream()
			.collect(Collectors.toMap(result -> UtilsJS.cast(result.key()), result -> UtilsJS.cast(result.tags())));

		if (!tags.containsKey(Registries.ITEM)) {
			ConsoleJS.getCurrent(ConsoleJS.SERVER).warn("Failed to load item tags during recipe event! Using replaceInput etc. will not work!");
			return TagContext.EMPTY;
		}

		return new TagContext() {
			@Override
			public <T> Iterable<Holder<T>> getTag(TagKey<T> tag) {
				return UtilsJS.cast(tags.get(tag.registry()).getOrDefault(tag.location(), Set.of()));
			}
		};
	}

	default <T> boolean isEmpty(TagKey<T> tag) {
		return Iterables.isEmpty(getTag(tag));
	}

	default <T> boolean contains(TagKey<T> tag, T value) {
		if (isEmpty(tag)) {
			return false;
		}

		for (var holder : getTag(tag)) {
			// if a mod doesn't do proper equality checks
			// on their content i'm gonna die i guess
			if (holder.value().equals(value)) {
				return true;
			}
		}

		return false;
	}

	default boolean areTagsBound() {
		return false;
	}

	;

	<T> Iterable<Holder<T>> getTag(TagKey<T> tag);

	default Collection<ItemStack> patchIngredientTags(TagKey<Item> tag) {
		var c = getTag(tag);

		var stacks = new ArrayList<ItemStack>(c instanceof Collection<?> cl ? cl.size() : 3);

		for (var holder : c) {
			stacks.add(new ItemStack(holder.value()));
		}

		return stacks.isEmpty() ? List.of() : stacks;
	}
}