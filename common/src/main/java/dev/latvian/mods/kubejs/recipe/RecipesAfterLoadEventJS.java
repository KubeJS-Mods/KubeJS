package dev.latvian.mods.kubejs.recipe;

import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.recipe.filter.RecipeFilter;
import dev.latvian.mods.kubejs.recipe.minecraft.CustomRecipeJS;
import dev.latvian.mods.kubejs.server.ServerSettings;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
public class RecipesAfterLoadEventJS extends EventJS {
	public static final EventHandler EVENT = EventHandler.server(RecipesAfterLoadEventJS.class).legacy("recipes.after_loaded");

	private final Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> recipeMap;
	private List<RecipeJS> originalRecipes;
	private final Set<RecipeJS> removedRecipes = new HashSet<>();

	private RecipesAfterLoadEventJS(Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> r) {
		recipeMap = r;
	}

	private List<RecipeJS> getOriginalRecipes() {
		if (originalRecipes == null) {
			originalRecipes = new ArrayList<>();

			for (var map : recipeMap.values()) {
				for (var entry : map.entrySet()) {
					RecipeJS r = new CustomRecipeJS();
					r.id = entry.getKey();
					r.originalRecipe = entry.getValue();
					r.type = new AfterLoadRecipeTypeJS(r.originalRecipe.getSerializer());
					originalRecipes.add(r);
				}
			}
		}

		return originalRecipes;
	}

	public void forEachRecipe(RecipeFilter filter, Consumer<RecipeJS> consumer) {
		if (filter == RecipeFilter.ALWAYS_TRUE) {
			getOriginalRecipes().forEach(consumer);
		} else if (filter != RecipeFilter.ALWAYS_FALSE) {
			getOriginalRecipes().stream().filter(filter).forEach(consumer);
		}
	}

	public void forEachRecipeAsync(RecipeFilter filter, Consumer<RecipeJS> consumer) {
		if (filter == RecipeFilter.ALWAYS_TRUE) {
			getOriginalRecipes().parallelStream().forEach(consumer);
		} else if (filter != RecipeFilter.ALWAYS_FALSE) {
			getOriginalRecipes().parallelStream().filter(filter).forEach(consumer);
		}
	}

	public int countRecipes(RecipeFilter filter) {
		if (filter == RecipeFilter.ALWAYS_TRUE) {
			return getOriginalRecipes().size();
		} else if (filter != RecipeFilter.ALWAYS_FALSE) {
			return (int) getOriginalRecipes().stream().filter(filter).count();
		}

		return 0;
	}

	public int remove(RecipeFilter filter) {
		var count = new MutableInt();
		forEachRecipe(filter, r ->
		{
			if (removedRecipes.add(r)) {
				if (ServerSettings.instance.logRemovedRecipes) {
					ConsoleJS.SERVER.info("- " + r + ": " + r.getFromToString());
				} else if (ConsoleJS.SERVER.shouldPrintDebug()) {
					ConsoleJS.SERVER.debug("- " + r + ": " + r.getFromToString());
				}

				count.increment();
			}
		});
		return count.getValue();
	}

	@HideFromJS
	public static void post(RecipeManager recipeManager) {
		var e = new RecipesAfterLoadEventJS(recipeManager.recipes);
		var b = ServerSettings.instance.useOriginalRecipeForFilters;
		ServerSettings.instance.useOriginalRecipeForFilters = true;
		RecipesAfterLoadEventJS.EVENT.post(e);
		ServerSettings.instance.useOriginalRecipeForFilters = b;

		if (e.originalRecipes != null) {
			e.originalRecipes.removeAll(e.removedRecipes);

			Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> newMap = new HashMap<>();
			Map<ResourceLocation, Recipe<?>> newByName = new HashMap<>();

			for (var r : e.originalRecipes) {
				newMap.computeIfAbsent(r.originalRecipe.getType(), t -> new HashMap<>()).put(r.id, r.originalRecipe);
				newByName.put(r.id, r.originalRecipe);
			}

			recipeManager.recipes = newMap;
			recipeManager.byName = newByName;
		}
	}
}