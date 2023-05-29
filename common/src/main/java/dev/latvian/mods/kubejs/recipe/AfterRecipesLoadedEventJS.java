package dev.latvian.mods.kubejs.recipe;

import dev.latvian.mods.kubejs.DevProperties;
import dev.latvian.mods.kubejs.core.RecipeKJS;
import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.event.EventResult;
import dev.latvian.mods.kubejs.recipe.filter.ConstantFilter;
import dev.latvian.mods.kubejs.recipe.filter.RecipeFilter;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class AfterRecipesLoadedEventJS extends EventJS {
	private final Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> recipeMap;
	private final Map<ResourceLocation, Recipe<?>> recipeIdMap;

	private List<RecipeKJS> originalRecipes;

	public AfterRecipesLoadedEventJS(Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> r, Map<ResourceLocation, Recipe<?>> n) {
		recipeMap = r;
		recipeIdMap = n;
	}

	private List<RecipeKJS> getOriginalRecipes() {
		if (originalRecipes == null) {
			originalRecipes = new ArrayList<>();

			for (var map : recipeMap.values()) {
				for (var entry : map.entrySet()) {
					originalRecipes.add((RecipeKJS) entry.getValue());
				}
			}
		}

		return originalRecipes;
	}

	public void forEachRecipe(RecipeFilter filter, Consumer<RecipeKJS> consumer) {
		if (filter == ConstantFilter.TRUE) {
			getOriginalRecipes().forEach(consumer);
		} else if (filter != ConstantFilter.FALSE) {
			getOriginalRecipes().stream().filter(filter).forEach(consumer);
		}
	}

	public int countRecipes(RecipeFilter filter) {
		if (filter == ConstantFilter.TRUE) {
			return getOriginalRecipes().size();
		} else if (filter != ConstantFilter.FALSE) {
			return (int) getOriginalRecipes().stream().filter(filter).count();
		}

		return 0;
	}

	public int remove(RecipeFilter filter) {
		int count = 0;
		var itr = getOriginalRecipes().iterator();

		while (itr.hasNext()) {
			var r = itr.next();

			if (filter.test(r)) {
				var map = recipeMap.get(((Recipe<?>) r).getType());

				if (map != null) {
					map.remove(r.kjs$getOrCreateId());
				}

				recipeIdMap.remove(r.kjs$getOrCreateId());
				itr.remove();
				count++;

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
	protected void afterPosted(EventResult isCanceled) {
		recipeMap.values().removeIf(Map::isEmpty);
	}
}