package dev.latvian.mods.kubejs.recipe;

import dev.latvian.mods.kubejs.DevProperties;
import dev.latvian.mods.kubejs.core.RecipeLikeKJS;
import dev.latvian.mods.kubejs.core.RecipeManagerKJS;
import dev.latvian.mods.kubejs.event.EventResult;
import dev.latvian.mods.kubejs.event.KubeEvent;
import dev.latvian.mods.kubejs.recipe.filter.ConstantFilter;
import dev.latvian.mods.kubejs.recipe.filter.RecipeFilter;
import dev.latvian.mods.kubejs.script.ConsoleJS;
import dev.latvian.mods.rhino.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public class AfterRecipesLoadedKubeEvent implements KubeEvent {
	private final RecipeManagerKJS recipeManager;
	private List<RecipeLikeKJS> originalRecipes;
	private boolean changed;

	public AfterRecipesLoadedKubeEvent(RecipeManagerKJS recipeManager) {
		this.recipeManager = recipeManager;
		this.originalRecipes = null;
		this.changed = false;
	}

	private List<RecipeLikeKJS> getOriginalRecipes() {
		if (originalRecipes == null) {
			originalRecipes = new ArrayList<>(recipeManager.kjs$getRecipeIdMap().values());
		}

		return originalRecipes;
	}

	public void forEachRecipe(Context cx, RecipeFilter filter, Consumer<RecipeLikeKJS> consumer) {
		if (filter == ConstantFilter.TRUE) {
			getOriginalRecipes().forEach(consumer);
		} else if (filter != ConstantFilter.FALSE) {
			getOriginalRecipes().stream().filter(r -> filter.test(cx, r)).forEach(consumer);
		}
	}

	public int countRecipes(Context cx, RecipeFilter filter) {
		if (filter == ConstantFilter.TRUE) {
			return getOriginalRecipes().size();
		} else if (filter != ConstantFilter.FALSE) {
			return (int) getOriginalRecipes().stream().filter(r -> filter.test(cx, r)).count();
		}

		return 0;
	}

	public int remove(Context cx, RecipeFilter filter) {
		int count = 0;
		var itr = getOriginalRecipes().iterator();

		while (itr.hasNext()) {
			var r = itr.next();

			if (filter.test(cx, r)) {
				itr.remove();
				count++;
				changed = true;

				if (DevProperties.get().logRemovedRecipes) {
					ConsoleJS.SERVER.info("- " + r);
				} else if (ConsoleJS.SERVER.shouldPrintDebug()) {
					ConsoleJS.SERVER.debug("- " + r);
				}
			}
		}

		return count;
	}

	@Override
	public void afterPosted(EventResult result) {
		if (changed) {
			var map = new HashMap<ResourceLocation, RecipeHolder<?>>();

			for (var r : getOriginalRecipes()) {
				map.put(r.kjs$getOrCreateId(), (RecipeHolder) r);
			}

			recipeManager.kjs$replaceRecipes(map);
		}
	}
}