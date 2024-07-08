package dev.latvian.mods.kubejs.registry;

import com.mojang.serialization.Codec;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import java.util.function.Consumer;

public interface BuilderTypeRegistry {
	interface Callback<T> {
		void addDefault(Class<? extends BuilderBase<? extends T>> builderType, BuilderFactory factory);

		void add(String type, Class<? extends BuilderBase<? extends T>> builderType, BuilderFactory factory);
	}

	<T> void of(ResourceKey<Registry<T>> registry, Consumer<Callback<T>> callback);

	default <T> void addDefault(ResourceKey<Registry<T>> registry, Class<? extends BuilderBase<? extends T>> builderType, BuilderFactory factory) {
		of(registry, reg -> reg.addDefault(builderType, factory));
	}

	<T> void serverRegistry(ResourceKey<Registry<T>> registry, Codec<T> directCodec, TypeInfo typeInfo);

	default <T> void serverRegistry(ResourceKey<Registry<T>> registry, Codec<T> directCodec, Class<T> type) {
		serverRegistry(registry, directCodec, TypeInfo.of(type));
	}
}
