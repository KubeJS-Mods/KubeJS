package dev.latvian.mods.kubejs.script;

import com.mojang.serialization.Codec;
import dev.architectury.registry.registries.Registrar;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.KubeJSRegistries;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.util.wrap.TypeWrapperFactory;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

public class RegistryTypeWrapperFactory<T> implements TypeWrapperFactory<T> {
	private static List<RegistryTypeWrapperFactory<?>> all;

	@SuppressWarnings({"rawtypes", "unchecked"})
	public static List<RegistryTypeWrapperFactory<?>> getAll() {
		if (all == null) {
			all = new ArrayList<>();

			try {
				for (var field : Registry.class.getDeclaredFields()) {
					if (field.getType() == ResourceKey.class && Modifier.isPublic(field.getModifiers()) && Modifier.isStatic(field.getModifiers())) {
						var id = "unknown";

						try {
							field.setAccessible(true);
							var key = (ResourceKey) field.get(null);
							id = key.location().getPath();
							var type = field.getGenericType(); // ResourceKey<Registry<T>>
							var type1 = ((ParameterizedType) type).getActualTypeArguments()[0]; // Registry<T>
							var type2 = ((ParameterizedType) type1).getActualTypeArguments()[0]; // T
							Class rawType = UtilsJS.getRawType(type2);

							if (rawType == Item.class || rawType == ResourceLocation.class || rawType == ResourceKey.class || rawType == Codec.class) {
								continue;
							}

							all.add(new RegistryTypeWrapperFactory(rawType, KubeJSRegistries.genericRegistry(key), key.location().toString()));
						} catch (Throwable t) {
							// KubeJS.LOGGER.error("Failed to create TypeWrapper for registry " + id + ": " + t);
						}
					}
				}
			} catch (Exception ex) {
				KubeJS.LOGGER.error("Failed to register TypeWrappers for registries!");
				ex.printStackTrace();
			}
		}

		return all;
	}

	public final Class<T> type;
	public final Registrar<T> registry;
	public final String name;

	private RegistryTypeWrapperFactory(Class<T> t, Registrar<T> r, String n) {
		type = t;
		registry = r;
		name = n;
	}

	@Override
	@SuppressWarnings("unchecked")
	public T wrap(Object o) {
		if (o == null) {
			return null;
		} else if (type.isAssignableFrom(o.getClass())) {
			return (T) o;
		}

		return registry.get(UtilsJS.getMCID(o));
	}

	@Override
	public String toString() {
		return "RegistryTypeWrapperFactory{type=" + type.getName() + ", registry=" + name + '}';
	}
}
