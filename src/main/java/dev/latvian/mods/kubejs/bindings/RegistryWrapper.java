package dev.latvian.mods.kubejs.bindings;

import dev.latvian.mods.kubejs.script.KubeJSContext;
import dev.latvian.mods.rhino.Context;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public record RegistryWrapper<T>(Registry<T> registry, ResourceKey<T> unknownKey) implements Iterable<T> {
	public static RegistryWrapper<?> of(Context cx, ResourceLocation id) {
		return ((KubeJSContext) cx).getRegistries().wrapRegistry(id);
	}

	public T get(ResourceLocation id) {
		return registry.get(id);
	}

	public boolean contains(ResourceLocation id) {
		return registry.containsKey(id);
	}

	public Set<Map.Entry<ResourceLocation, T>> getEntrySet() {
		return registry.entrySet().stream().map(e -> Map.entry(e.getKey().location(), e.getValue())).collect(Collectors.toSet());
	}

	public Map<ResourceLocation, T> getValueMap() {
		return registry.entrySet().stream().collect(Collectors.toMap(e -> e.getKey().location(), Map.Entry::getValue));
	}

	public List<T> getValues() {
		return registry.stream().collect(Collectors.toList());
	}

	public Set<ResourceLocation> getKeys() {
		return registry.keySet();
	}

	@Nullable
	public ResourceLocation getId(T value) {
		return registry.getKey(value);
	}

	@Nullable
	public ResourceKey<T> getKey(T value) {
		return registry.getResourceKey(value).orElse(unknownKey);
	}

	@NotNull
	@Override
	public ListIterator<T> iterator() {
		return getValues().listIterator();
	}
}
