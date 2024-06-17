package dev.latvian.mods.kubejs.integration.rei;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.recipe.viewer.RemoveRecipesKubeEvent;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.CollectionUtils;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class REIRemoveRecipeKubeEvent implements RemoveRecipesKubeEvent {
	private final Map<CategoryIdentifier<?>, Collection<ResourceLocation>> recipesRemoved;
	private final CategoryRegistry categories;

	public REIRemoveRecipeKubeEvent(Map<CategoryIdentifier<?>, Collection<ResourceLocation>> recipesRemoved) {
		this.recipesRemoved = recipesRemoved;
		this.categories = CategoryRegistry.getInstance();
	}

	@Override
	public void remove(ResourceLocation[] recipesToRemove) {
		var asList = List.of(recipesToRemove);

		for (var catId : categories) {
			recipesRemoved.computeIfAbsent(catId.getCategoryIdentifier(), _0 -> new HashSet<>()).addAll(asList);
		}
	}

	@Override
	public void removeFromCategory(ResourceLocation category, ResourceLocation[] recipesToRemove) {
		var catId = CategoryIdentifier.of(category);

		if (categories.tryGet(catId).isEmpty()) {
			KubeJS.LOGGER.warn("Failed to remove recipes for type {}: Category doesn't exist!", category);
			KubeJS.LOGGER.info("Use event.categoryIds to get a list of all categories.");
			return;
		}

		recipesRemoved.computeIfAbsent(catId, _0 -> new HashSet<>()).addAll(List.of(recipesToRemove));
	}

	@Override
	public Collection<ResourceLocation> getCategories() {
		return CollectionUtils.map(categories, CategoryRegistry.CategoryConfiguration::getIdentifier);
	}
}
