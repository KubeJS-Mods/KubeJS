package dev.latvian.mods.kubejs.registry;

import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugins;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.util.Lazy;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public record BuilderTypeRegistryHandler(Map<ResourceKey<?>, Info<?>> map) implements BuilderTypeRegistry, ServerRegistryRegistry {
	public static final Lazy<Map<ResourceKey<?>, Info<?>>> INFO = Lazy.identityMap(map -> {
		var handler = new BuilderTypeRegistryHandler(map);
		KubeJSPlugins.forEachPlugin(handler, KubeJSPlugin::registerBuilderTypes);
		KubeJSPlugins.forEachPlugin(handler, KubeJSPlugin::registerServerRegistries);
	});

	public static <T> Info<T> info(ResourceKey<? extends Registry<T>> key) {
		return (Info<T>) INFO.get().get(key);
	}

	public static class Info<T> {
		private @Nullable BuilderType<T> defaultType;
		private @Nullable Map<Identifier, BuilderType<T>> types;
		private @Nullable Map<String, BuilderType<T>> fallbackLookup;
		private @Nullable Codec<T> directCodec;
		private @Nullable TypeInfo typeInfo;

		@Nullable
		public BuilderType<T> defaultType() {
			return defaultType;
		}

		public List<BuilderType<T>> types() {
			return types == null ? List.of() : List.copyOf(types.values());
		}

		@Nullable
		public BuilderType<T> namedType(Identifier name) {
			var t = types == null ? null : types.get(name);
			return t != null ? t : fallbackLookup == null ? null : fallbackLookup.get(name.getPath());
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
	public <T> void of(ResourceKey<? extends Registry<T>> registry, Consumer<Callback<T>> callback) {
		callback.accept(new RegConsumer<>((Info) map.computeIfAbsent(registry, k -> new Info<>())));
	}

	@Override
	public <T> void register(ResourceKey<? extends Registry<T>> registry, Codec<T> directCodec, TypeInfo typeInfo) {
		var info = map.computeIfAbsent(registry, k -> new Info<>());
		info.directCodec = (Codec) directCodec;
		info.typeInfo = typeInfo == null ? TypeInfo.NONE : typeInfo;
	}


	private record RegConsumer<T>(Info<T> info) implements BuilderTypeRegistry.Callback<T> {
		private static final Identifier DEFAULT = KubeJS.id("default");

		@Override
		public void addDefault(Class<? extends BuilderBase<? extends T>> builderType, BuilderFactory factory) {
			if (info.defaultType != null) {
				ScriptType.STARTUP.console.warn("Previous default type '" + info.defaultType.builderClass().getName() + "' for registry '" + info + "' replaced with '" + builderType.getName() + "'!");
			}

			info.defaultType = new BuilderType<>(DEFAULT, builderType, factory);
		}

		@Override
		public void add(Identifier type, Class<? extends BuilderBase<? extends T>> builderType, BuilderFactory factory) {
			if (info.types == null) {
				info.types = new LinkedHashMap<>();
			}

			if (info.fallbackLookup == null) {
				info.fallbackLookup = new HashMap<>();
			}

			var prev = info.types.get(type);

			if (prev != null) {
				ScriptType.STARTUP.console.warn("Previous '" + type + "' type '" + prev.builderClass().getName() + "' for registry '" + info + "' replaced with '" + builderType.getName() + "'!");
			}

			var builderTypeDef = new BuilderType<>(type, builderType, factory);
			info.types.put(type, builderTypeDef);
			info.fallbackLookup.put(type.getPath(), builderTypeDef);
		}
	}
}
