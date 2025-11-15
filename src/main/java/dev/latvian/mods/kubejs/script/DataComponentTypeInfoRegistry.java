package dev.latvian.mods.kubejs.script;

import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.core.component.DataComponentType;

import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;

@FunctionalInterface
public interface DataComponentTypeInfoRegistry {
	void register(DataComponentType<?> type, TypeInfo typeInfo);

	default void scanClass(Class<?> clz) {
		try {
			for (var field : clz.getDeclaredFields()) {
				if (field.getType() == DataComponentType.class
					&& Modifier.isPublic(field.getModifiers())
					&& Modifier.isStatic(field.getModifiers())
					&& field.getGenericType() instanceof ParameterizedType t) {
					var key = (DataComponentType<?>) field.get(null);
					var typeInfo = TypeInfo.of(t.getActualTypeArguments()[0]);
					register(key, typeInfo);
				}
			}
		} catch (Exception ex) {
			// TODO: better exception handling?
			ex.printStackTrace();
		}
	}
}
