package dev.latvian.mods.kubejs.integration.rei;

import dev.latvian.mods.kubejs.event.KubeEvent;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry.CategoryConfiguration;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.CollectionUtils;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;

public class RemoveREICategoryKubeEvent implements KubeEvent {
	private final Set<CategoryIdentifier<?>> categoriesRemoved;
	private final CategoryRegistry registry;

	public RemoveREICategoryKubeEvent(Set<CategoryIdentifier<?>> categoriesRemoved) {
		this.categoriesRemoved = categoriesRemoved;
		registry = CategoryRegistry.getInstance();
	}

	public CategoryRegistry getRegistry() {
		return registry;
	}

	public CategoryRegistry getCategories() {
		return registry;
	}

	public Collection<ResourceLocation> getCategoryIds() {
		return CollectionUtils.map(registry, CategoryConfiguration::getIdentifier);
	}

	public void remove(ResourceLocation... categories) {
		for (var id : categories) {
			categoriesRemoved.add(CategoryIdentifier.of(id));
		}
	}

	public void removeIf(Predicate<CategoryConfiguration<?>> filter) {
		registry.stream()
			.filter(filter)
			.map(CategoryConfiguration::getIdentifier)
			.forEach(this::remove);
	}
}