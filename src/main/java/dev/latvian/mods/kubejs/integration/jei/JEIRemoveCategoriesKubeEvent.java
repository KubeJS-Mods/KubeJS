package dev.latvian.mods.kubejs.integration.jei;

import dev.latvian.mods.kubejs.recipe.viewer.RemoveCategoriesKubeEvent;
import dev.latvian.mods.rhino.Context;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.Map;

public class JEIRemoveCategoriesKubeEvent implements RemoveCategoriesKubeEvent {
	private final IRecipeManager recipeManager;
	private final Map<ResourceLocation, IRecipeCategory<?>> categories;

	public JEIRemoveCategoriesKubeEvent(IRecipeManager recipeManager, Map<ResourceLocation, IRecipeCategory<?>> categories) {
		this.recipeManager = recipeManager;
		this.categories = categories;
	}

	@Override
	public void remove(Context cx, ResourceLocation[] ids) {
		for (var c : ids) {
			var category = categories.get(c);

			if (category != null) {
				recipeManager.hideRecipeCategory(category.getRecipeType());
				categories.remove(c);
			}
		}
	}

	@Override
	public Collection<ResourceLocation> getCategories() {
		return categories.keySet();
	}
}