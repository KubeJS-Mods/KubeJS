package dev.latvian.mods.kubejs.integration.jei;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.event.EventResult;
import mezz.jei.api.recipe.IRecipeCategoriesLookup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class RemoveJEICategoriesEvent extends EventJS {
	private final IJeiRuntime runtime;
	private final Collection<RecipeType<?>> categoriesRemoved;
	private final IRecipeCategoriesLookup categoryLookup;

	public RemoveJEICategoriesEvent(IJeiRuntime r) {
		runtime = r;
		categoriesRemoved = new HashSet<>();
		categoryLookup = runtime.getRecipeManager().createRecipeCategoryLookup();
	}

	public Collection<IRecipeCategory<?>> getCategories() {
		return categoryLookup.get().toList();
	}

	public void remove(ResourceLocation... categoriesToYeet) {
		var idSet = Set.of(categoriesToYeet);
		categoryLookup.get()
			.map(IRecipeCategory::getRecipeType)
			.filter(type -> idSet.contains(type.getUid()))
			.forEach(categoriesRemoved::add);
	}

	public Collection<ResourceLocation> getCategoryIds() {
		return categoryLookup.get().map(IRecipeCategory::getRecipeType).map(RecipeType::getUid).toList();
	}

	public void removeIf(Predicate<IRecipeCategory<?>> filter) {
		categoryLookup.get()
			.filter(filter)
			.map(IRecipeCategory::getRecipeType)
			.forEach(categoriesRemoved::add);
	}

	@Override
	protected void afterPosted(EventResult result) {
		for (var category : categoriesRemoved) {
			try {
				runtime.getRecipeManager().hideRecipeCategory(category);
			} catch (Exception e) {
				KubeJS.LOGGER.warn("Failed to yeet recipe category {}!", category);
			}
		}
	}
}