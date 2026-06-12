package dev.latvian.mods.kubejs.integration.jei;

import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import org.jspecify.annotations.Nullable;

import java.util.List;

public record DataComponentTypeInterpreter<T>(List<DataComponentType<?>> keys) implements ISubtypeInterpreter<T> {
	public static final DataComponentTypeInterpreter<?> EMPTY = new DataComponentTypeInterpreter<>(List.of());

	@SuppressWarnings("unchecked")
	public static <T> DataComponentTypeInterpreter<T> of(List<DataComponentType<?>> keys) {
		return keys.isEmpty() ? (DataComponentTypeInterpreter<T>) EMPTY : new DataComponentTypeInterpreter<>(keys);
	}

	@Override
	@Nullable
	public Object getSubtypeData(T from, UidContext context) {
		if (!(from instanceof DataComponentHolder holder)) {
			return "";
		}

		if (keys.isEmpty()) {
			var sb = new StringBuilder();

			for (var entry : holder.getComponents()) {
				sb.append(BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(entry.type()));

				var o = entry.value();

				if (o != null) {
					sb.append(o);
				} else {
					sb.append('!');
				}
			}

			return sb.toString();
		} else if (keys.size() == 1) {
			var o = holder.getComponents().get(keys.getFirst());
			return o == null ? "" : o.toString();
		} else {
			var sb = new StringBuilder();

			for (var key : keys) {
				var o = holder.getComponents().get(key);

				if (o != null) {
					sb.append(o);
				} else {
					sb.append('!');
				}
			}

			return sb.toString();
		}
	}
}