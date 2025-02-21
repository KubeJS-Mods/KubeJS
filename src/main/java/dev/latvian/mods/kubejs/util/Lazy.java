package dev.latvian.mods.kubejs.util;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;

import java.time.Duration;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Lazy<T> implements Supplier<T> {
	public static <T> Lazy<T> of(Supplier<T> supplier) {
		return new Lazy<>(supplier, 0L);
	}

	public static <T> Lazy<T> of(Supplier<T> supplier, Duration expires) {
		return new Lazy<>(supplier, expires.toMillis());
	}

	public static <T> Lazy<T> serviceLoader(Class<T> type) {
		return of(() -> {
			var value = ServiceLoader.load(type).findFirst();

			if (value.isEmpty()) {
				throw new RuntimeException("Could not find platform implementation for %s!".formatted(type.getSimpleName()));
			}

			return value.get();
		});
	}

	public static <K, V> Lazy<Map<K, V>> map(Consumer<Map<K, V>> supplier) {
		return Lazy.of(() -> {
			var map = new Object2ObjectOpenHashMap<K, V>();
			supplier.accept(map);
			return Map.copyOf(map);
		});
	}

	public static <K, V> Lazy<Map<K, V>> identityMap(Consumer<Map<K, V>> supplier) {
		return Lazy.of(() -> {
			var map = new Reference2ObjectOpenHashMap<K, V>();
			supplier.accept(map);

			if (map.isEmpty()) {
				return Reference2ObjectMaps.emptyMap();
			} else if (map.size() == 1) {
				var first = map.entrySet().iterator().next();
				return Reference2ObjectMaps.singleton(first.getKey(), first.getValue());
			} else {
				return Reference2ObjectMaps.unmodifiable(map);
			}
		});
	}

	private final Supplier<T> factory;
	private final long expiresAfter;
	private T value;
	private boolean cached;
	private long expires;

	private Lazy(Supplier<T> factory, long expiresAfter) {
		this.factory = factory;
		this.expiresAfter = expiresAfter;
		this.value = null;
		this.cached = false;
		this.expires = 0L;
	}

	@Override
	public T get() {
		if (expires > 0L && System.currentTimeMillis() > expires) {
			cached = false;
		} else if (cached) {
			return value;
		}

		value = factory.get();
		cached = true;

		if (expiresAfter > 0L) {
			expires = System.currentTimeMillis() + expiresAfter;
		}

		return value;
	}

	public void forget() {
		value = null;
		cached = false;
		expires = 0L;
	}
}
