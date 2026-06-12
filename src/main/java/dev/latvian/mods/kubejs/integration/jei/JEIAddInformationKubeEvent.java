package dev.latvian.mods.kubejs.integration.jei;

import dev.latvian.mods.kubejs.recipe.viewer.AddInformationKubeEvent;
import dev.latvian.mods.kubejs.recipe.viewer.RecipeViewerEntryType;
import dev.latvian.mods.rhino.Context;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.network.chat.Component;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

public class JEIAddInformationKubeEvent implements AddInformationKubeEvent {
	private final RecipeViewerEntryType type;
	private final IIngredientType ingredientType;
	private final IRecipeRegistration registration;
	private Collection allIngredients;

	public JEIAddInformationKubeEvent(RecipeViewerEntryType type, IIngredientType<?> ingredientType, IRecipeRegistration registration) {
		this.type = type;
		this.ingredientType = ingredientType;
		this.registration = registration;
	}

	@Override
	public void add(Context cx, Object filter, List<Component> info) {
		var in = (Predicate) type.wrapPredicate(cx, filter);

		if (allIngredients == null) {
			var manager = registration.getIngredientManager();
			allIngredients = manager.getAllIngredients(ingredientType);
		}

		var infoArr = info.toArray(new Component[0]);

		for (var v : allIngredients) {
			if (in.test(v)) {
				registration.addIngredientInfo(v, ingredientType, infoArr);
			}
		}
	}
}
