package dev.latvian.mods.kubejs.recipe;

import com.mojang.datafixers.util.Either;
import dev.latvian.mods.kubejs.KubeJS;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.TagLoader;
import net.minecraft.util.DependencySorter;
import net.minecraft.world.item.Items;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class CachedTagLookup<T> {
	public record Entry<T>(ResourceKey<T> key, Registry<T> registry, CachedTagLookup<T> lookup) {
	}

	record SortingEntry(List<TagLoader.EntryWithSource> entries) implements DependencySorter.Entry<ResourceLocation> {
		@Override
		public void visitRequiredDependencies(Consumer<ResourceLocation> visitor) {
			this.entries.forEach(arg -> arg.entry().visitRequiredDependencies(visitor));
		}

		@Override
		public void visitOptionalDependencies(Consumer<ResourceLocation> visitor) {
			this.entries.forEach(arg -> arg.entry().visitOptionalDependencies(visitor));
		}
	}

	public final Registry<T> registry;
	public final Map<ResourceLocation, List<TagLoader.EntryWithSource>> originalMap;
	private Map<ResourceLocation, Collection<Holder<T>>> tagMap;
	private Map<TagKey<T>, Set<T>> keyToValue;
	private Map<T, Set<TagKey<T>>> valueToKey;

	public CachedTagLookup(Registry<T> registry, Map<ResourceLocation, List<TagLoader.EntryWithSource>> originalMap) {
		this.registry = registry;
		this.originalMap = originalMap;
	}

	private Either<Collection<TagLoader.EntryWithSource>, Collection<T>> build(TagEntry.Lookup<T> lookup, List<TagLoader.EntryWithSource> entries) {
		var builder = new LinkedHashSet<T>();
		var list = new ArrayList<TagLoader.EntryWithSource>();
		for (var tagloader$entrywithsource : entries) {
			if (tagloader$entrywithsource.entry().build(lookup, tagloader$entrywithsource.remove() ? builder::remove : builder::add) || tagloader$entrywithsource.remove()) {
				continue;
			}

			list.add(tagloader$entrywithsource);
		}
		return list.isEmpty() ? Either.right(List.copyOf(builder)) : Either.left(list);
	}

	public Map<ResourceLocation, Collection<T>> build(Map<ResourceLocation, List<TagLoader.EntryWithSource>> builders) {
		var map = new HashMap<ResourceLocation, Collection<T>>();
		var lookup = new TagEntry.Lookup<T>() {
			@Override
			@Nullable
			public T element(ResourceLocation id) {
				return registry.getOptional(id).orElse(null);
			}

			@Override
			@Nullable
			public Collection<T> tag(ResourceLocation id) {
				return map.get(id);
			}
		};

		var dependencysorter = new DependencySorter<ResourceLocation, SortingEntry>();
		builders.forEach((arg, list) -> dependencysorter.addEntry(arg, new SortingEntry(list)));
		dependencysorter.orderByDependencies((arg, arg2) -> this.build(lookup, arg2.entries).ifLeft(collection -> KubeJS.LOGGER.error("Couldn't load tag {} as it is missing following references: {}", arg, collection.stream().map(Objects::toString).collect(Collectors.joining("\n\t", "\n\t", "")))).ifRight(collection -> map.put(arg, collection)));
		return map;
	}

	public Map<TagKey<T>, Set<T>> keyToValue() {
		if (keyToValue == null) {
			var map = build(originalMap);
			keyToValue = new Reference2ObjectOpenHashMap<>(map.size());

			for (var entry : map.entrySet()) {
				var k = TagKey.create(registry.key(), entry.getKey());
				keyToValue.put(k, Set.copyOf(entry.getValue()));
			}
		}

		return keyToValue;

	}

	public Set<T> values(TagKey<T> key) {
		return keyToValue().getOrDefault(key, Set.of());
	}

	public boolean isEmpty(TagKey<T> key) {
		var set = values(key);
		// noinspection RedundantCast,rawtypes,SuspiciousMethodCalls
		return set.size() - ((ResourceKey) registry.key() == Registries.ITEM ? ((set.contains(Items.AIR) ? 1 : 0) + (set.contains(Items.BARRIER) ? 1 : 0)) : 0) <= 0;
	}

	public Set<TagKey<T>> keys(T value) {
		if (valueToKey == null) {
			valueToKey = new Reference2ObjectOpenHashMap<>();

			for (var entry : keyToValue().entrySet()) {
				for (var v : entry.getValue()) {
					valueToKey.computeIfAbsent(v, k -> new HashSet<>()).add(entry.getKey());
				}
			}
		}

		return valueToKey.getOrDefault(value, Set.of());
	}

	public Map<TagKey<T>, List<Holder<T>>> bindingMap() {
		var k2v = keyToValue();
		var map = new Reference2ObjectOpenHashMap<TagKey<T>, List<Holder<T>>>(k2v.size());

		for (var entry : k2v.entrySet()) {
			var list = new ArrayList<Holder<T>>(entry.getValue().size());

			for (var value : entry.getValue()) {
				list.add(registry.wrapAsHolder(value));
			}

			map.put(entry.getKey(), list);
		}

		return map;
	}

	public Map<ResourceLocation, Collection<Holder<T>>> tagMap() {
		if (tagMap == null) {
			tagMap = new HashMap<>();
			var k2v = keyToValue();

			for (var entry : k2v.entrySet()) {
				var list = new ArrayList<Holder<T>>(entry.getValue().size());

				for (var value : entry.getValue()) {
					list.add(registry.wrapAsHolder(value));
				}

				tagMap.put(entry.getKey().location(), list);
			}

			tagMap = Map.copyOf(tagMap);
		}

		return tagMap;
	}
}
