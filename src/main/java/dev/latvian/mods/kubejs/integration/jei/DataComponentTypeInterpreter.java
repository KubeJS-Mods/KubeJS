package dev.latvian.mods.kubejs.integration.jei;

import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.List;

public record DataComponentTypeInterpreter(List<DataComponentType<?>> keys) implements IIngredientSubtypeInterpreter {
	public static final DataComponentTypeInterpreter EMPTY = new DataComponentTypeInterpreter(List.of());

	public static DataComponentTypeInterpreter of(List<DataComponentType<?>> keys) {
		return keys.isEmpty() ? EMPTY : new DataComponentTypeInterpreter(keys);
	}

	@Override
	public String apply(Object from, UidContext context) {
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