package dev.latvian.mods.kubejs.registry;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public interface BuilderTypeRegistry {
	interface Callback<T> {
		void addDefault(Class<? extends BuilderBase<? extends T>> builderType, BuilderFactory factory);

		void add(ResourceLocation type, Class<? extends BuilderBase<? extends T>> builderType, BuilderFactory factory);
	}

	<T> void of(ResourceKey<Registry<T>> registry, Consumer<Callback<T>> callback);

	default <T> void addDefault(ResourceKey<Registry<T>> registry, Class<? extends BuilderBase<? extends T>> builderType, BuilderFactory factory) {
		of(registry, reg -> reg.addDefault(builderType, factory));
	}
}
