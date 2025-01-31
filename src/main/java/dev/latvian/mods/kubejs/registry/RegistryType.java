package dev.latvian.mods.kubejs.registry;

import dev.latvian.mods.kubejs.DevProperties;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.JavaWrapper;
import dev.latvian.mods.rhino.type.TypeInfo;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

public record RegistryType<T>(ResourceKey<Registry<T>> key, Class<?> baseClass, TypeInfo type) {
	private static final Map<ResourceKey<?>, RegistryType<?>> KEY_MAP = new Reference2ObjectOpenHashMap<>();
	private static final Map<TypeInfo, RegistryType<?>> TYPE_MAP = new HashMap<>();
	private static final Map<Class<?>, List<RegistryType<?>>> CLASS_MAP = new Reference2ObjectOpenHashMap<>();

	public static synchronized <T> void register(ResourceKey<Registry<T>> key, TypeInfo type) {
		var t = new RegistryType<>(key, type.asClass(), type);
		KEY_MAP.put(key, t);
		TYPE_MAP.put(type, t);
		CLASS_MAP.computeIfAbsent(t.baseClass, c -> new ArrayList<>(1)).add(t);

		if (DevProperties.get().logRegistryTypes) {
			KubeJS.LOGGER.info("Registered RegistryType '" + key.location() + "': " + type);
		}
	}

	@Nullable
	public static synchronized RegistryType<?> ofKey(ResourceKey<?> key) {
		return (RegistryType<?>) of(key);
	}

	@Nullable
	public static synchronized RegistryType<?> ofType(TypeInfo typeInfo) {
		return (RegistryType<?>) of(typeInfo);
	}

	@Nullable
	public static synchronized RegistryType<?> ofClass(Class<?> type) {
		var regList = ((List<RegistryType<?>>) of(type));
		return regList != null && regList.size() == 1 ? regList.getFirst() : null;
	}

	public static synchronized List<RegistryType<?>> allOfClass(Class<?> type) {
		return (List<RegistryType<?>>) of(type);
	}

	private static synchronized Object of(Object obj) {
		Scanner.startIfNotFrozen();
		return switch (obj) {
			case ResourceKey key -> KEY_MAP.get(key);
			case Class clazz -> CLASS_MAP.getOrDefault(clazz, List.of());
			case TypeInfo info -> TYPE_MAP.get(info);
			default -> List.of();
		};
	}

	@Nullable
	public static synchronized RegistryType<?> lookup(TypeInfo target) {
		var reg = allOfClass(target.asClass());

		if (reg.size() == 1) {
			return reg.getFirst();
		} else if (!reg.isEmpty()) {
			for (var regType : reg) {
				if (regType.type().equals(target)) {
					return regType;
				}
			}
		}

		return null;
	}

	@Override
	public String toString() {
		return key.location() + "=" + type;
	}

	public static class Scanner {
		private static final Lazy<Set<Class<?>>> VALID_TYPES = Lazy.of(() -> {
			Set<Class<?>> set = new HashSet<>();
			set.add(ResourceKey.class);
			set.add(Registry.class);
			var registrar = JavaWrapper.tryLoadClass("dev.architectury.registry.registries.Registrar");
			if (registrar != null) {
				set.add(registrar);
			}
			return set;
		});

		private static final Set<String> CLASSES_TO_SCAN = new HashSet<>();
		private static final Set<String> MODULES_TO_SKIP = Set.of("java.base", "neoforge", "fml_loader", "kubejs");
		private static final Set<String> NAMESPACES_TO_SKIP = Set.of("neoforge", "minecraft");

		private static boolean frozen = false;

		public static synchronized void init() {
			processClass(Stream.of(Registries.class, NeoForgeRegistries.Keys.class));
		}

		private static synchronized void startIfNotFrozen() {
			if (frozen) {
				return;
			}

			frozen = true;
			var startTime = Util.getNanos();
			processClass(CLASSES_TO_SCAN.stream().map(JavaWrapper::tryLoadClass));
			CLASSES_TO_SCAN.clear();
			KubeJS.LOGGER.debug("Took {} ms to discover registry classes.", (int) ((Util.getNanos() - startTime) / 1_000_000));
		}

		private static void processClass(Stream<Class<?>> classStream) {
			classStream
				.filter(Objects::nonNull)
				.map(Class::getDeclaredFields)
				.flatMap(Stream::of)
				.forEach(field -> {
					try {
						if (!VALID_TYPES.get().contains(field.getType()) || !Modifier.isStatic(field.getModifiers())) {
							return;
						}

						if (!Modifier.isPublic(field.getModifiers())) {
							field.setAccessible(true);
						}

						var value = field.get(null);

						if (value instanceof ResourceKey<?> key) {
							if (field.getGenericType() instanceof ParameterizedType t1
								&& t1.getActualTypeArguments()[0] instanceof ParameterizedType t2) {
								processKey(key, t2, false);
							}
						} else if (value instanceof Registry<?> registry) {
							if (field.getGenericType() instanceof ParameterizedType t1) {
								processKey(registry.key(), t1, true);
							}
						} else if (field.getType().getName().equals("dev.architectury.registry.registries.Registrar")) {
							if (field.getGenericType() instanceof ParameterizedType t1) {
								var method = value.getClass().getDeclaredMethod("key");
								processKey((ResourceKey) method.invoke(value), t1, true);
							}
						}
					} catch (Exception ex) {
						KubeJS.LOGGER.error("Error while trying to get registry from field " + field.getName() + " from class " + field.getType().getName(), ex);
					}
				});
		}

		private static void processKey(ResourceKey key, ParameterizedType paramType, boolean checkIfContains) {
			if (checkIfContains && RegistryType.ofKey(key) != null) {
				return;
			}
			var type = paramType.getActualTypeArguments()[0];
			var typeInfo = TypeInfo.of(type);
			register(key, typeInfo);
		}

		public static synchronized void scan(ResourceLocation registryName, ResourceLocation location) {
			if (frozen) {
				return;
			}

			if (!registryName.equals(Registries.ROOT_REGISTRY_NAME)) {
				return;
			}

			if (NAMESPACES_TO_SKIP.contains(location.getNamespace())) {
				return;
			}

			var startTime = Util.getNanos();
			var stack = Thread.currentThread().getStackTrace();

			for (var stackTraceElement : stack) {
				var moduleName = stackTraceElement.getModuleName();
				if (moduleName != null && MODULES_TO_SKIP.contains(moduleName)) {
					continue;
				}

				var className = stackTraceElement.getClassName();

				if (CLASSES_TO_SCAN.contains(className)) {
					continue;
				}

				CLASSES_TO_SCAN.add(className);
			}

			KubeJS.LOGGER.debug("Took {} ms to grab stacktrace classes.", (int) ((Util.getNanos() - startTime) / 1_000_000));
		}
	}
}
