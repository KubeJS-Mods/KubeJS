package dev.latvian.kubejs.integration.jei;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.event.EventJS;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * @author LatvianModder
 */
public class YeetJEIRecipesEvent extends EventJS {
	private final IJeiRuntime runtime;
	private final HashMap<ResourceLocation, Collection<ResourceLocation>> recipesYeeted;
	private final Collection<IRecipeCategory<?>> allCategories;

	public YeetJEIRecipesEvent(IJeiRuntime r) {
		runtime = r;
		recipesYeeted = new HashMap<>();
		allCategories = runtime.getRecipeManager().getRecipeCategories();
	}

	public Collection<IRecipeCategory<?>> getCategories() {
		return allCategories;
	}

	public Collection<ResourceLocation> getCategoryIds() {
		Set<ResourceLocation> set = new HashSet<>();
		for (IRecipeCategory<?> allCategory : allCategories) {
			ResourceLocation uid = allCategory.getUid();
			set.add(uid);
		}
		return set;
	}

	public void yeet(String category, String... recipesToYeet) {
		ResourceLocation cat = new ResourceLocation(category);
		for (String toYeet : recipesToYeet) {
			recipesYeeted.computeIfAbsent(cat, _0 -> new HashSet<>()).add(new ResourceLocation(toYeet));
		}
	}

	@Override
	protected void afterPosted(boolean result) {
		IRecipeManager rm = runtime.getRecipeManager();
		for (ResourceLocation cat : recipesYeeted.keySet()) {
			try {
				IRecipeCategory<?> category = rm.getRecipeCategory(cat);
				if (Recipe.class.isAssignableFrom(category.getRecipeClass())) {
					for (ResourceLocation id : recipesYeeted.get(cat)) {
						try {
							boolean found = false;
							for (Object o : rm.getRecipes(category)) {
								Recipe<?> recipe = (Recipe<?>) o;
								if (id.equals(recipe.getId())) {
									rm.hideRecipe(recipe, cat);
									found = true;
									break;
								}
							}
							if (!found) {
								KubeJS.LOGGER.warn("Failed to yeet recipe {} for category {}: Recipe doesn't exist!", id, cat);
							}
						} catch (Exception e) {
							KubeJS.LOGGER.warn("Failed to yeet recipe {} for category {}: An unexpected error was thrown!", id, cat);
						}
					}
				} else {
					KubeJS.LOGGER.warn("Failed to yeet recipes for category {}: Recipe type is unsupported!", cat);
				}
			} catch (NullPointerException | IllegalStateException e) {
				KubeJS.LOGGER.warn("Failed to yeet recipes for category {}: Category doesn't exist!", cat);
			}
		}
	}
}