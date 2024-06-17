package dev.latvian.mods.kubejs.integration.rei;

import dev.latvian.mods.kubejs.recipe.viewer.RemoveCategoriesKubeEvent;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry.CategoryConfiguration;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.CollectionUtils;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.Set;

public class REIRemoveCategoriesKubeEvent implements RemoveCategoriesKubeEvent {
	private final Set<CategoryIdentifier<?>> categoriesRemoved;
	private final CategoryRegistry registry;

	public REIRemoveCategoriesKubeEvent(Set<CategoryIdentifier<?>> categoriesRemoved) {
		this.categoriesRemoved = categoriesRemoved;
		this.registry = CategoryRegistry.getInstance();
	}

	@Override
	public void remove(ResourceLocation[] categories) {
		for (var id : categories) {
			categoriesRemoved.add(CategoryIdentifier.of(id));
		}
	}

	@Override
	public Collection<ResourceLocation> getCategories() {
		return CollectionUtils.map(registry, CategoryConfiguration::getIdentifier);
	}
}