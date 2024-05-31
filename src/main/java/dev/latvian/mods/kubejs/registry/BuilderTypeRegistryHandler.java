package dev.latvian.mods.kubejs.registry;

import dev.latvian.mods.kubejs.util.ConsoleJS;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import java.util.LinkedHashMap;
import java.util.function.Consumer;

public class BuilderTypeRegistryHandler implements BuilderTypeRegistry {
	@Override
	public <T> void of(ResourceKey<Registry<T>> registry, Consumer<Callback<T>> callback) {
		callback.accept(new RegConsumer<>(RegistryInfo.of(registry)));
	}

	private record RegConsumer<T>(RegistryInfo<T> registryInfo) implements BuilderTypeRegistry.Callback<T> {
		@Override
		public void addDefault(Class<? extends BuilderBase<? extends T>> builderType, BuilderFactory factory) {
			if (registryInfo.defaultType != null) {
				ConsoleJS.STARTUP.warn("Previous default type '" + registryInfo.defaultType.builderClass().getName() + "' for registry '" + registryInfo + "' replaced with '" + builderType.getName() + "'!");
			}

			registryInfo.defaultType = new BuilderType<>("default", builderType, factory);
		}

		@Override
		public void add(String type, Class<? extends BuilderBase<? extends T>> builderType, BuilderFactory factory) {
			if (registryInfo.types == null) {
				registryInfo.types = new LinkedHashMap<>();
			}

			var prev = registryInfo.types.get(type);

			if (prev != null) {
				ConsoleJS.STARTUP.warn("Previous '" + type + "' type '" + prev.builderClass().getName() + "' for registry '" + registryInfo + "' replaced with '" + builderType.getName() + "'!");
			}

			registryInfo.types.put(type, new BuilderType<>(type, builderType, factory));
		}
	}
}
