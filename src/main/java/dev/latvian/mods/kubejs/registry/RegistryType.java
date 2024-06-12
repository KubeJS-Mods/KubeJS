package dev.latvian.mods.kubejs.registry;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.neoforged.fml.loading.FMLLoader;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public record RegistryType<T>(ResourceKey<Registry<T>> key, Class<?> baseClass, TypeInfo type) {
	private static final Map<ResourceKey<?>, RegistryType<?>> KEY_MAP = new IdentityHashMap<>();
	private static final Map<TypeInfo, RegistryType<?>> TYPE_MAP = new HashMap<>();
	private static final Map<Class<?>, List<RegistryType<?>>> CLASS_MAP = new IdentityHashMap<>();

	// This is cursed, but it's better than manually registering every type
	public static synchronized void init() {
		try {
			for (var field : Registries.class.getDeclaredFields()) {
				if (field.getType() == ResourceKey.class
					&& Modifier.isPublic(field.getModifiers())
					&& Modifier.isStatic(field.getModifiers())
					&& field.getGenericType() instanceof ParameterizedType t1
					&& t1.getActualTypeArguments()[0] instanceof ParameterizedType t2
				) {
					var key = (ResourceKey) field.get(null);
					var type = t2.getActualTypeArguments()[0];
					var typeInfo = TypeInfo.of(type);
					register(key, typeInfo);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static synchronized <T> void register(ResourceKey<Registry<T>> key, TypeInfo type) {
		var t = new RegistryType<>(key, type.asClass(), type);
		KEY_MAP.put(key, t);
		TYPE_MAP.put(type, t);
		CLASS_MAP.computeIfAbsent(t.baseClass, c -> new ArrayList<>(1)).add(t);

		if (!FMLLoader.isProduction()) {
			KubeJS.LOGGER.info("Registered RegistryType '" + key.location() + "': " + type);
		}
	}

	@Nullable
	public static synchronized RegistryType<?> ofKey(ResourceKey<?> key) {
		return KEY_MAP.get(key);
	}

	@Nullable
	public static synchronized RegistryType<?> ofType(TypeInfo typeInfo) {
		return TYPE_MAP.get(typeInfo);
	}

	@Nullable
	public static synchronized RegistryType<?> ofClass(Class<?> type) {
		var list = CLASS_MAP.get(type);
		return list != null && list.size() == 1 ? list.getFirst() : null;
	}

	public static synchronized List<RegistryType<?>> allOfClass(Class<?> type) {
		return CLASS_MAP.getOrDefault(type, List.of());
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
}
