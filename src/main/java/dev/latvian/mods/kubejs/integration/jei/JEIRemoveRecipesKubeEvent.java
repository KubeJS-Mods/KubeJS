package dev.latvian.mods.kubejs.integration.jei;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.event.EventResult;
import dev.latvian.mods.kubejs.recipe.viewer.RemoveRecipesKubeEvent;
import dev.latvian.mods.rhino.Context;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@SuppressWarnings({"rawtypes", "unchecked"})
public class JEIRemoveRecipesKubeEvent implements RemoveRecipesKubeEvent {
	private final IRecipeManager recipeManager;
	private final Map<ResourceLocation, IRecipeCategory> categories;
	private final Set<ResourceLocation> removedGlobal;
	private final Map<IRecipeCategory, Collection<ResourceLocation>> removed;

	public JEIRemoveRecipesKubeEvent(IRecipeManager recipeManager, Map<ResourceLocation, IRecipeCategory<?>> categories) {
		this.recipeManager = recipeManager;
		this.categories = (Map) categories;
		this.removedGlobal = new HashSet<>();
		this.removed = new Reference2ObjectOpenHashMap<>();
	}

	@Override
	public void remove(Context cx, ResourceLocation[] recipesToRemove) {
		for (var cat : categories.values()) {
			removed.computeIfAbsent(cat, _0 -> new HashSet<>()).addAll(Arrays.asList(recipesToRemove));
		}
	}

	@Override
	public void removeFromCategory(Context cx, @Nullable ResourceLocation category, ResourceLocation[] recipesToRemove) {
		if (category == null) {
			remove(cx, recipesToRemove);
			return;
		}

		var cat = categories.get(category);

		if (cat == null) {
			KubeJS.LOGGER.info("Failed to remove recipes for type '" + category + "': Category doesn't exist! Use event.categories to get a list of all categories.");
			return;
		}

		removed.computeIfAbsent(cat, _0 -> new HashSet<>()).addAll(Arrays.asList(recipesToRemove));
	}

	@Override
	public void afterPosted(EventResult result) {
		for (var cat : categories.values()) {
			var removedCat = removed.get(cat);

			if ((removedCat == null || removedCat.isEmpty()) && removedGlobal.isEmpty()) {
				continue;
			}

			var allRecipes = recipeManager.createRecipeLookup(cat.getRecipeType()).get().toList();
			var removedRecipes = new ArrayList<>();

			for (var recipe : allRecipes) {
				var id = cat.getRegistryName(recipe);

				if (id != null && ((removedCat != null && removedCat.contains(id)) || removedGlobal.contains(id))) {
					removedRecipes.add(recipe);
				}
			}

			if (!removedRecipes.isEmpty()) {
				recipeManager.hideRecipes(cat.getRecipeType(), removedRecipes);
			}
		}
	}
}