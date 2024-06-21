package dev.latvian.mods.kubejs.integration.jei;

import dev.latvian.mods.kubejs.recipe.viewer.RecipeViewerEntryType;
import dev.latvian.mods.kubejs.recipe.viewer.RegisterSubtypesKubeEvent;
import dev.latvian.mods.kubejs.recipe.viewer.SubtypeInterpreter;
import dev.latvian.mods.rhino.Context;
import mezz.jei.api.ingredients.IIngredientTypeWithSubtypes;
import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.core.component.DataComponentType;

import java.util.List;

public class JEIRegisterSubtypesKubeEvent implements RegisterSubtypesKubeEvent {
	public record JEISubtypeInterpreter(SubtypeInterpreter interpreter) implements IIngredientSubtypeInterpreter {
		@Override
		public String apply(Object ingredient, UidContext context) {
			var o = interpreter.apply(ingredient);
			return o == null ? "" : o.toString();
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
	public void useComponents(Context cx, Object filter, List<DataComponentType<?>> components) {
		var in = DataComponentTypeInterpreter.of(components);

		for (var item : JEIIntegration.getEntries(type, cx, filter)) {
			registration.registerSubtypeInterpreter(ingredientType, type.getBase(item), in);
		}
	}
}