package dev.latvian.kubejs.script;

import com.mojang.serialization.Codec;
import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.KubeJSRegistries;
import dev.latvian.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.util.wrap.TypeWrapperFactory;
import me.shedaniel.architectury.registry.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class RegistryTypeWrapperFactory<T> implements TypeWrapperFactory<T> {
	private static List<RegistryTypeWrapperFactory<?>> all;

	@SuppressWarnings({"rawtypes", "unchecked"})
	public static List<RegistryTypeWrapperFactory<?>> getAll() {
		if (all == null) {
			all = new ArrayList<>();

			try {
				for (Field field : net.minecraft.core.Registry.class.getDeclaredFields()) {
					if (field.getType() == ResourceKey.class && Modifier.isPublic(field.getModifiers()) && Modifier.isStatic(field.getModifiers())) {
						String id = "unknown";

						try {
							field.setAccessible(true);
							ResourceKey key = (ResourceKey) field.get(null);
							id = key.location().getPath();
							Type type = field.getGenericType(); // ResourceKey<Registry<T>>
							Type type1 = ((ParameterizedType) type).getActualTypeArguments()[0]; // Registry<T>
							Type type2 = ((ParameterizedType) type1).getActualTypeArguments()[0]; // T
							Class rawType = UtilsJS.getRawType(type2);

							if (rawType == Item.class || rawType == ResourceLocation.class || rawType == ResourceKey.class || rawType == Codec.class) {
								continue;
							}

							all.add(new RegistryTypeWrapperFactory(rawType, KubeJSRegistries.genericRegistry(key), key.location().toString()));
						} catch (Throwable t) {
							KubeJS.LOGGER.error("Failed to create TypeWrapper for registry " + id + ": " + t);
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
	public final Registry<T> registry;
	public final String name;

	private RegistryTypeWrapperFactory(Class<T> t, Registry<T> r, String n) {
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
