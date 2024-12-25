package dev.latvian.mods.kubejs.util;

import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public interface NameProvider<T> {
	interface Registry<K, T> {
		void register(K key, NameProvider<T> provider);

		default void register(List<K> keys, NameProvider<T> provider) {
			for (var key : keys) {
				register(key, provider);
			}
		}
	}

	static <K, T> Map<K, NameProvider<T>> create(Consumer<Registry<K, T>> registry) {
		var map = new HashMap<K, NameProvider<T>>();
		registry.accept(map::put);
		return map;
	}

	@Nullable
	Component getName(RegistryAccess registries, T value);
}
