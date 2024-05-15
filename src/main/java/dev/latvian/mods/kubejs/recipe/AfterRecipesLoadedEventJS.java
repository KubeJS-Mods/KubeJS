package dev.latvian.mods.kubejs.recipe;

import com.google.common.collect.Multimap;
import dev.latvian.mods.kubejs.DevProperties;
import dev.latvian.mods.kubejs.core.RecipeLikeKJS;
import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.recipe.filter.ConstantFilter;
import dev.latvian.mods.kubejs.recipe.filter.RecipeFilter;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class AfterRecipesLoadedEventJS extends EventJS {
	private final Multimap<RecipeType<?>, RecipeHolder<?>> recipeTypeMap;
	private final Map<ResourceLocation, RecipeHolder<?>> recipeIdMap;

	private List<RecipeLikeKJS> originalRecipes;

	public AfterRecipesLoadedEventJS(Multimap<RecipeType<?>, RecipeHolder<?>> recipeTypeMap, Map<ResourceLocation, RecipeHolder<?>> recipeIdMap) {
		this.recipeTypeMap = recipeTypeMap;
		this.recipeIdMap = recipeIdMap;
	}

	private List<RecipeLikeKJS> getOriginalRecipes() {
		if (originalRecipes == null) {
			originalRecipes = new ArrayList<>(recipeIdMap.values());
		}

		return originalRecipes;
	}

	public void forEachRecipe(RecipeFilter filter, Consumer<RecipeLikeKJS> consumer) {
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
				var holder = recipeIdMap.remove(r.kjs$getOrCreateId());
				recipeTypeMap.values().remove(holder);

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
}