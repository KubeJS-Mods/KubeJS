package dev.latvian.mods.kubejs.plugin.builtin.wrapper;

import dev.latvian.mods.kubejs.error.KubeRuntimeException;
import dev.latvian.mods.kubejs.holder.HolderWrapper;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.Context;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public record RegistryWrapper<T>(Registry<T> registry, @Nullable ResourceKey<T> unknownKey) implements Iterable<T> {
	public static RegistryWrapper<?> of(Context cx, Identifier id) {
		return RegistryAccessContainer.of(cx).wrapRegistry(id);
	}

	public static RegistryAccessContainer access() {
		return RegistryAccessContainer.current;
	}

	public T get(Identifier id) {
		return registry.getOptional(id).orElseThrow(() -> new KubeRuntimeException("Unknown registry entry: " + id + " in " + registry.key().identifier()));
	}

	public boolean contains(Identifier id) {
		return registry.containsKey(id);
	}

	public boolean containsValue(T value) {
		return registry.containsValue(value);
	}

	public Set<Map.Entry<Identifier, T>> getEntrySet() {
		return registry.entrySet().stream().map(e -> Map.entry(e.getKey().identifier(), e.getValue())).collect(Collectors.toSet());
	}

	public Map<Identifier, T> getValueMap() {
		return registry.entrySet().stream().collect(Collectors.toMap(e -> e.getKey().identifier(), Map.Entry::getValue));
	}

	public HolderSetWrapper<T> getValues(Object filter) {
		var holderSet = HolderWrapper.wrapSimpleSet(registry, filter);
		return new HolderSetWrapper<>(registry, Objects.requireNonNullElseGet(holderSet, HolderSet::empty));
	}

	<A> DataMapWrapper<T, A> getDataMap(DataMapType<T, A> type) {
		return new DataMapWrapper<>(registry, type);
	}

	public DataMapWrapper<T, ?> getDataMap(Identifier id) {
		return DataMapWrapper.of(this, id);
	}

	public List<T> getValues() {
		return registry.stream().collect(Collectors.toList());
	}

	public Set<Identifier> getKeys() {
		return registry.keySet();
	}

	@Nullable
	public T getRandom() {
		return getRandom(UtilsJS.RANDOM);
	}

	@Nullable
	public T getRandom(RandomSource random) {
		return registry.getRandom(random).map(Holder::value).orElse(null);
	}

	@Nullable
	public Identifier getId(T value) {
		return registry.getKey(value);
	}

	@Nullable
	public ResourceKey<T> getKey(T value) {
		return registry.getResourceKey(value).orElse(unknownKey);
	}

	@Override
	public ListIterator<T> iterator() {
		return getValues().listIterator();
	}
}
