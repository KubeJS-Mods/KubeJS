package dev.latvian.mods.kubejs.registry;

import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugins;
import dev.latvian.mods.kubejs.script.ConsoleJS;
import dev.latvian.mods.kubejs.util.Lazy;
import dev.latvian.mods.rhino.type.TypeInfo;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public record BuilderTypeRegistryHandler(Map<ResourceKey<?>, Info<?>> map) implements BuilderTypeRegistry, ServerRegistryRegistry {
	public static final Lazy<Map<ResourceKey<?>, Info<?>>> INFO = Lazy.of(() -> {
		var handler = new BuilderTypeRegistryHandler(new Reference2ObjectOpenHashMap<>());
		KubeJSPlugins.forEachPlugin(handler, KubeJSPlugin::registerBuilderTypes);
		KubeJSPlugins.forEachPlugin(handler, KubeJSPlugin::registerServerRegistries);
		return handler.map;
	});

	public static <T> Info<T> info(ResourceKey<Registry<T>> key) {
		return (Info<T>) INFO.get().get(key);
	}

	public static class Info<T> {
		private static final Info<?> EMPTY = new Info<>();

		private BuilderType<T> defaultType;
		private Map<String, BuilderType<T>> types;
		private Codec<T> directCodec;
		private TypeInfo typeInfo;

		@Nullable
		public BuilderType<T> defaultType() {
			return defaultType;
		}

		public List<BuilderType<T>> types() {
			return types == null ? List.of() : List.copyOf(types.values());
		}

		@Nullable
		public BuilderType<T> namedType(String name) {
			return types == null ? null : types.get(name);
		}

		@Nullable
		public Codec<T> directCodec() {
			return directCodec;
		}

		@Nullable
		public TypeInfo typeInfo() {
			return typeInfo;
		}
	}

	@Override
	public <T> void of(ResourceKey<Registry<T>> registry, Consumer<Callback<T>> callback) {
		callback.accept(new RegConsumer<>((Info) map.computeIfAbsent(registry, k -> new Info<>())));
	}

	@Override
	public <T> void register(ResourceKey<Registry<T>> registry, Codec<T> directCodec, TypeInfo typeInfo) {
		var info = map.computeIfAbsent(registry, k -> new Info<>());
		info.directCodec = (Codec) directCodec;
		info.typeInfo = typeInfo == null ? TypeInfo.NONE : typeInfo;
	}

	private record RegConsumer<T>(Info<T> info) implements BuilderTypeRegistry.Callback<T> {
		@Override
		public void addDefault(Class<? extends BuilderBase<? extends T>> builderType, BuilderFactory factory) {
			if (info.defaultType != null) {
				ConsoleJS.STARTUP.warn("Previous default type '" + info.defaultType.builderClass().getName() + "' for registry '" + info + "' replaced with '" + builderType.getName() + "'!");
			}

			info.defaultType = new BuilderType<>("default", builderType, factory);
		}

		@Override
		public void add(String type, Class<? extends BuilderBase<? extends T>> builderType, BuilderFactory factory) {
			if (info.types == null) {
				info.types = new LinkedHashMap<>();
			}

			var prev = info.types.get(type);

			if (prev != null) {
				ConsoleJS.STARTUP.warn("Previous '" + type + "' type '" + prev.builderClass().getName() + "' for registry '" + info + "' replaced with '" + builderType.getName() + "'!");
			}

			info.types.put(type, new BuilderType<>(type, builderType, factory));
		}
	}
}
