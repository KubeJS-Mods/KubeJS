package dev.latvian.mods.kubejs.integration.rei;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.event.KubeEvent;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.CollectionUtils;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class RemoveREIRecipeKubeEvent implements KubeEvent {
	private final Map<CategoryIdentifier<?>, Collection<ResourceLocation>> recipesRemoved;
	private final CategoryRegistry categories;
	private final DisplayRegistry displays;

	public RemoveREIRecipeKubeEvent(Map<CategoryIdentifier<?>, Collection<ResourceLocation>> recipesRemoved) {
		this.recipesRemoved = recipesRemoved;
		this.categories = CategoryRegistry.getInstance();
		this.displays = DisplayRegistry.getInstance();
	}


	public CategoryRegistry getCategories() {
		return categories;
	}

	public DisplayRegistry getDisplays() {
		return displays;
	}

	public List<?> getDisplaysFor(ResourceLocation category) {
		return displays.get(CategoryIdentifier.of(category));
	}

	public Collection<ResourceLocation> getCategoryIds() {
		return CollectionUtils.map(categories, CategoryRegistry.CategoryConfiguration::getIdentifier);
	}

	public void remove(ResourceLocation category, ResourceLocation... recipesToRemove) {
		var catId = CategoryIdentifier.of(category);
		if (categories.tryGet(catId).isEmpty()) {
			KubeJS.LOGGER.warn("Failed to remove recipes for type {}: Category doesn't exist!", category);
			KubeJS.LOGGER.info("Use event.categoryIds to get a list of all categories.");
			return;
		}
		recipesRemoved.computeIfAbsent(catId, _0 -> new HashSet<>()).addAll(List.of(recipesToRemove));
	}

	public void removeFromAll(ResourceLocation... recipesToRemove) {
		var asList = List.of(recipesToRemove);
		for (var catId : categories) {
			recipesRemoved.computeIfAbsent(catId.getCategoryIdentifier(), _0 -> new HashSet<>()).addAll(asList);
		}
	}
}
