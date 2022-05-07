package dev.latvian.mods.kubejs.integration.forge.jei;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.event.EventJS;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * @author LatvianModder
 */
public class RemoveJEIRecipesEvent extends EventJS {
	private final IJeiRuntime runtime;
	private final HashMap<ResourceLocation, Collection<ResourceLocation>> recipesRemoved;
	private final Collection<IRecipeCategory<?>> allCategories;

	public RemoveJEIRecipesEvent(IJeiRuntime r) {
		runtime = r;
		recipesRemoved = new HashMap<>();
		allCategories = runtime.getRecipeManager().getRecipeCategories(Collections.emptyList(), false);
	}

	public Collection<IRecipeCategory<?>> getCategories() {
		return allCategories;
	}

	public Collection<ResourceLocation> getCategoryIds() {
		Set<ResourceLocation> set = new HashSet<>();
		for (var allCategory : allCategories) {
			set.add(allCategory.getUid());
		}
		return set;
	}

	public void remove(ResourceLocation category, ResourceLocation[] recipesToRemove) {
		for (var toRemove : recipesToRemove) {
			recipesRemoved.computeIfAbsent(category, _0 -> new HashSet<>()).add(toRemove);
		}
	}

	public void yeet(ResourceLocation category, ResourceLocation[] recipesToYeet) {
		remove(category, recipesToYeet);
	}

	@Override
	protected void afterPosted(boolean result) {
		var rm = runtime.getRecipeManager();
		for (var cat : recipesRemoved.keySet()) {
			try {
				var category = rm.getRecipeCategory(cat, false);
				if (Recipe.class.isAssignableFrom(category.getRecipeClass())) {
					for (var id : recipesRemoved.get(cat)) {
						try {
							var found = false;
							for (Object o : rm.getRecipes(category, Collections.emptyList(), false)) {
								var recipe = (Recipe<?>) o;
								if (id.equals(recipe.getId())) {
									rm.hideRecipe(recipe, cat);
									found = true;
									break;
								}
							}
							if (!found) {
								KubeJS.LOGGER.warn("Failed to remove recipe {} for category {}: Recipe doesn't exist!", id, cat);
							}
						} catch (Exception e) {
							KubeJS.LOGGER.warn("Failed to remove recipe {} for category {}: An unexpected error was thrown!", id, cat);
						}
					}
				} else {
					KubeJS.LOGGER.warn("Failed to remove recipes for category {}: Recipe type is unsupported!", cat);
				}
			} catch (NullPointerException | IllegalStateException e) {
				KubeJS.LOGGER.warn("Failed to remove recipes for category {}: Category doesn't exist!", cat);
				KubeJS.LOGGER.info("Available categories: " + getCategoryIds());
			}
		}
	}
}