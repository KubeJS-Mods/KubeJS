package dev.latvian.mods.kubejs.plugin.builtin.wrapper;

import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import dev.latvian.mods.rhino.Context;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.RegistryManager;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;

public record DataMapWrapper<T, A>(Registry<T> registry, DataMapType<T, A> type) implements Iterable<DataMapWrapper.Data<T, A>> {
	public static DataMapWrapper<?, ?> of(Context cx, ResourceLocation registry, ResourceLocation id) {
		var reg = RegistryAccessContainer.of(cx).wrapRegistry(registry);
		return of(reg, id);
	}

	public static DataMapType<?, ?> typeOf(Context cx, ResourceLocation registry, ResourceLocation id) {
		var reg = RegistryAccessContainer.of(cx).wrapRegistry(registry);
		return typeOf(reg, id);
	}

	public static <T> DataMapWrapper<T, ?> of(RegistryWrapper<T> registry, ResourceLocation id) {
		var type = typeOf(registry, id);
		return new DataMapWrapper<>(registry.registry(), type);
	}

	@SuppressWarnings("UnstableApiUsage")
	public static <T> DataMapType<T, ?> typeOf(RegistryWrapper<T> registry, ResourceLocation id) {
		return RegistryManager.getDataMap(registry.registry().key(), id);
	}

	@Nullable
	public A get(T item) {
		return registry.getData(type, registry.getResourceKey(item).orElseThrow());
	}

	public Stream<T> keys() {
		return byKey().keySet().stream().map(registry::get);
	}

	public @NotNull Iterator<Data<T, A>> iterator() {
		return new Iterator<>() {
			final Iterator<Map.Entry<ResourceKey<T>, A>> backing = byKey().entrySet().iterator();

			@Override
			public boolean hasNext() {
				return backing.hasNext();
			}

			@Override
			public Data<T, A> next() {
				var entry = backing.next();
				return new Data<>(registry.get(entry.getKey()), entry.getValue());
			}
		};
	}

	public @NotNull Map<ResourceKey<T>, A> byKey() {
		return registry.getDataMap(type);
	}

	public record Data<T, A>(T element, A data) {
	}
}
