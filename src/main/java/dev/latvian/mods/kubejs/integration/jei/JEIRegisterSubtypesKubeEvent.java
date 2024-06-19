package dev.latvian.mods.kubejs.integration.jei;

import dev.latvian.mods.kubejs.recipe.viewer.RecipeViewerEntryType;
import dev.latvian.mods.kubejs.recipe.viewer.RegisterSubtypesKubeEvent;
import dev.latvian.mods.kubejs.recipe.viewer.SubtypeInterpreter;
import dev.latvian.mods.rhino.Context;
import mezz.jei.api.ingredients.IIngredientTypeWithSubtypes;
import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;

public class JEIRegisterSubtypesKubeEvent implements RegisterSubtypesKubeEvent {
	public record JEISubtypeInterpreter(SubtypeInterpreter interpreter) implements IIngredientSubtypeInterpreter {
		@Override
		public String apply(Object ingredient, UidContext context) {
			var o = interpreter.apply(ingredient);
			return o == null ? "" : o.toString();
		}
	}

	public record DataComponentTypeInterpreter(DataComponentType<?>[] keys) implements IIngredientSubtypeInterpreter {
		@Override
		public String apply(Object from, UidContext context) {
			if (!(from instanceof DataComponentHolder holder)) {
				return "";
			}

			if (keys.length == 1) {
				var o = holder.getComponents().get(keys[0]);
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

	public record AllDataComponentTypeInterpreter() implements IIngredientSubtypeInterpreter {
		@Override
		public String apply(Object from, UidContext context) {
			if (!(from instanceof DataComponentHolder holder)) {
				return "";
			}

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
		}
	}

	private final RecipeViewerEntryType type;
	private final IIngredientTypeWithSubtypes ingredientType;
	private final ISubtypeRegistration registration;

	public JEIRegisterSubtypesKubeEvent(RecipeViewerEntryType type, IIngredientTypeWithSubtypes<?, ?> ingredientType, ISubtypeRegistration registration) {
		this.type = type;
		this.ingredientType = ingredientType;
		this.registration = registration;
	}

	@Override
	public void register(Context cx, Object filter, SubtypeInterpreter interpreter) {
		var in = new JEISubtypeInterpreter(interpreter);

		for (var item : JEIIntegration.getEntries(type, cx, filter)) {
			registration.registerSubtypeInterpreter(ingredientType, type.getBase(item), in);
		}
	}

	@Override
	public void useComponents(Context cx, Object filter) {
		var in = new AllDataComponentTypeInterpreter();

		for (var item : JEIIntegration.getEntries(type, cx, filter)) {
			registration.registerSubtypeInterpreter(ingredientType, type.getBase(item), in);
		}
	}

	@Override
	public void useComponents(Context cx, Object filter, DataComponentType<?>[] components) {
		var in = new DataComponentTypeInterpreter(components);

		for (var item : JEIIntegration.getEntries(type, cx, filter)) {
			registration.registerSubtypeInterpreter(ingredientType, type.getBase(item), in);
		}
	}
}