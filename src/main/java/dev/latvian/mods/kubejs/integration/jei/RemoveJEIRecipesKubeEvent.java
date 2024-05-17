package dev.latvian.mods.kubejs.integration.jei;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.event.EventResult;
import dev.latvian.mods.kubejs.event.KubeEvent;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@SuppressWarnings({"rawtypes", "unchecked"})
public class RemoveJEIRecipesKubeEvent implements KubeEvent {
	private final IJeiRuntime runtime;
	private final Map<IRecipeCategory, Collection<ResourceLocation>> recipesRemoved;
	private final Map<ResourceLocation, IRecipeCategory> categoryById;

	public RemoveJEIRecipesKubeEvent(IJeiRuntime r) {
		runtime = r;
		recipesRemoved = new HashMap<>();
		categoryById = runtime.getRecipeManager().createRecipeCategoryLookup()
			.get()
			.collect(Collectors.toMap(cat -> cat.getRecipeType().getUid(), Function.identity()));
	}

	public Collection<IRecipeCategory> getCategories() {
		return categoryById.values();
	}

	public Collection<ResourceLocation> getCategoryIds() {
		return categoryById.keySet();
	}

	public void remove(ResourceLocation category, ResourceLocation[] recipesToRemove) {
		for (var toRemove : recipesToRemove) {
			if (!categoryById.containsKey(category)) {
				KubeJS.LOGGER.warn("Failed to remove recipes for type {}: Category doesn't exist!", category);
				KubeJS.LOGGER.info("Use event.categoryIds to get a list of all categories.");
				continue;
			}
			recipesRemoved.computeIfAbsent(categoryById.get(category), _0 -> new HashSet<>()).add(toRemove);
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