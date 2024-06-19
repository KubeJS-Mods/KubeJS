package dev.latvian.mods.kubejs.integration.jei;

import dev.latvian.mods.kubejs.recipe.viewer.RemoveCategoriesKubeEvent;
import dev.latvian.mods.rhino.Context;
import mezz.jei.api.recipe.IRecipeCategoriesLookup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.Set;

public class JEIRemoveCategoriesKubeEvent implements RemoveCategoriesKubeEvent {
	private final IJeiRuntime runtime;
	private final IRecipeCategoriesLookup categoryLookup;

	public JEIRemoveCategoriesKubeEvent(IJeiRuntime r) {
		this.runtime = r;
		this.categoryLookup = runtime.getRecipeManager().createRecipeCategoryLookup();
	}

	@Override
	public void remove(Context cx, ResourceLocation[] categories) {
		var idSet = Set.of(categories);
		categoryLookup.get()
			.map(IRecipeCategory::getRecipeType)
			.filter(type -> idSet.contains(type.getUid()))
			.forEach(runtime.getRecipeManager()::hideRecipeCategory);
	}

	@Override
	public Collection<ResourceLocation> getCategories() {
		return categoryLookup.get().map(IRecipeCategory::getRecipeType).map(RecipeType::getUid).toList();
	}
}