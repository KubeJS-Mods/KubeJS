package dev.latvian.mods.kubejs.integration.jei;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.event.EventResult;
import dev.latvian.mods.kubejs.recipe.viewer.RemoveRecipesKubeEvent;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.rhino.Context;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@SuppressWarnings({"rawtypes", "unchecked"})
public class JEIRemoveRecipesKubeEvent implements RemoveRecipesKubeEvent {
	private final IJeiRuntime runtime;
	private final Map<IRecipeCategory, Collection<ResourceLocation>> recipesRemoved;
	private final Map<ResourceLocation, IRecipeCategory> categoryById;

	public JEIRemoveRecipesKubeEvent(IJeiRuntime r) {
		this.runtime = r;
		this.recipesRemoved = new IdentityHashMap<>();
		this.categoryById = runtime.getRecipeManager().createRecipeCategoryLookup()
			.get()
			.collect(Collectors.toMap(cat -> cat.getRecipeType().getUid(), Function.identity()));
	}

	@Override
	public Collection<ResourceLocation> getCategories() {
		return categoryById.keySet();
	}

	@Override
	public void remove(Context cx, ResourceLocation[] recipesToRemove) {
		for (var category : categoryById.values()) {
			recipesRemoved.computeIfAbsent(category, _0 -> new HashSet<>()).addAll(Set.of(recipesToRemove));
		}
	}

	@Override
	public void removeFromCategory(Context cx, ResourceLocation category, ResourceLocation[] recipesToRemove) {
		for (var toRemove : recipesToRemove) {
			var cat = categoryById.get(category);

			if (cat == null) {
				ConsoleJS.CLIENT.warn("Failed to remove recipes for type '" + category + "': Category doesn't exist! Use event.categories to get a list of all categories.");
				continue;
			}

			recipesRemoved.computeIfAbsent(cat, _0 -> new HashSet<>()).add(toRemove);
		}
	}

	@Override
	public void afterPosted(EventResult result) {
		var rm = runtime.getRecipeManager();
		for (var cat : recipesRemoved.keySet()) {
			var type = cat.getRecipeType();
			var allRecipes = rm.createRecipeLookup(cat.getRecipeType()).get().toList();
			var ids = recipesRemoved.get(cat);
			var recipesHidden = new HashSet<>(ids.size());

			for (var id : ids) {
				var found = false;
				for (var recipe : allRecipes) {
					var recipeId = cat.getRegistryName(recipe);

					if (recipeId == null) {
						KubeJS.LOGGER.warn("Failed to remove recipe {} for type {}: Category does not support removal by id!", id, type);
						break;
					}

					if (recipeId.equals(id)) {
						found = true;
						recipesHidden.add(recipe);
						break;
					}

				}

				if (!found) {
					KubeJS.LOGGER.warn("Failed to remove recipe {} for type {}: Recipe doesn't exist!", id, type);
				}
			}

			rm.hideRecipes(type, recipesHidden);
		}
	}
}