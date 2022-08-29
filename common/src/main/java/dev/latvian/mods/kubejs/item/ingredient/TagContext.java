package dev.latvian.mods.kubejs.item.ingredient;

import com.google.common.collect.Iterables;
import dev.latvian.mods.kubejs.KubeJS;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.TagManager;
import net.minecraft.world.item.Item;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface TagContext {
	TagContext EMPTY = new TagContext() {
		@Override
		public boolean isEmpty(TagKey<Item> tag) {
			return true;
		}

		@Override
		public boolean areTagsBound() {
			return false;
		}

		@Override
		public Iterable<Holder<Item>> getTag(TagKey<Item> tag) {
			KubeJS.LOGGER.warn("Tried to get tag {} from an empty tag context!", tag.location());
			return List.of();
		}
	};

	TagContext REGISTRY = new TagContext() {
		@Override
		public boolean isEmpty(TagKey<Item> tag) {
			return Registry.ITEM.getTag(tag).isEmpty();
		}

		@Override
		public boolean areTagsBound() {
			return true;
		}

		@Override
		public Iterable<Holder<Item>> getTag(TagKey<Item> tag) {
			return Registry.ITEM.getTagOrEmpty(tag);
		}
	};

	MutableObject<TagContext> INSTANCE = new MutableObject<>(TagContext.EMPTY);

	static TagContext usingResult(TagManager.LoadResult<Item> manager) {
		return new TagContext() {
			@Override
			public boolean isEmpty(TagKey<Item> tag) {
				return Iterables.isEmpty(getTag(tag));
			}

			@Override
			public boolean areTagsBound() {
				return false;
			}

			@Override
			public Iterable<Holder<Item>> getTag(TagKey<Item> tag) {
				return manager.tags().getOrDefault(tag.location(), Set.of());
			}
		};
	}

	record Result(TagContext context, Collection<Holder<Item>> holders) {
	}

	boolean isEmpty(TagKey<Item> tag);

	boolean areTagsBound();

	Iterable<Holder<Item>> getTag(TagKey<Item> tag);
}