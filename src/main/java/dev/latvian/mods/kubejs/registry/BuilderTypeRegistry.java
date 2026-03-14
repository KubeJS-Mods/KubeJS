package dev.latvian.mods.kubejs.registry;

import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

import java.util.function.Consumer;

public interface BuilderTypeRegistry {
	interface Callback<T> {
		void addDefault(Class<? extends BuilderBase<? extends T>> builderType, BuilderFactory factory);

		void add(Identifier type, Class<? extends BuilderBase<? extends T>> builderType, BuilderFactory factory);
	}

	<T> void of(ResourceKey<? extends Registry<T>> registry, Consumer<Callback<T>> callback);

	default <T> void addDefault(ResourceKey<? extends Registry<T>> registry, Class<? extends BuilderBase<? extends T>> builderType, BuilderFactory factory) {
		of(registry, reg -> reg.addDefault(builderType, factory));
	}
}
