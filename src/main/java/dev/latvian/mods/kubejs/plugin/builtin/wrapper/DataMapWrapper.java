package dev.latvian.mods.kubejs.plugin.builtin.wrapper;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;

public record DataMapWrapper<T, A>(Registry<T> registry, DataMapType<T, A> type) implements Iterable<DataMapWrapper.Data<T, A>> {
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
