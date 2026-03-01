package dev.latvian.mods.kubejs.integration.jei;
/*

import dev.latvian.mods.kubejs.recipe.viewer.RemoveCategoriesKubeEvent;
import dev.latvian.mods.rhino.Context;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.resources.Identifier;

import java.util.Map;

public class JEIRemoveCategoriesKubeEvent implements RemoveCategoriesKubeEvent {
	private final IRecipeManager recipeManager;
	private final Map<Identifier, IRecipeCategory<?>> categories;

	public JEIRemoveCategoriesKubeEvent(IRecipeManager recipeManager, Map<Identifier, IRecipeCategory<?>> categories) {
		this.recipeManager = recipeManager;
		this.categories = categories;
	}

	@Override
	public void remove(Context cx, Identifier[] ids) {
		for (var c : ids) {
			var category = categories.get(c);

			if (category != null) {
				recipeManager.hideRecipeCategory(category.getRecipeType());
				categories.remove(c);
			}
		}
	}
}*/
