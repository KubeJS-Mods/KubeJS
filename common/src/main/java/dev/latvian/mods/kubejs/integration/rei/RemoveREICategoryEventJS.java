package dev.latvian.mods.kubejs.integration.rei;

import dev.latvian.mods.kubejs.event.EventJS;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry.CategoryConfiguration;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.CollectionUtils;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;

public class RemoveREICategoryEventJS extends EventJS {
	private final Set<CategoryIdentifier<?>> categoriesRemoved;
	private final CategoryRegistry registry;

	public RemoveREICategoryEventJS(Set<CategoryIdentifier<?>> categoriesRemoved) {
		this.categoriesRemoved = categoriesRemoved;
		registry = CategoryRegistry.getInstance();
	}

	public CategoryRegistry getRegistry() {
		return registry;
	}

	public CategoryRegistry getCategories() {
		return registry;
	}

	public Collection<String> getCategoryIds() {
		return CollectionUtils.map(registry, category -> category.getIdentifier().toString());
	}

	public void remove(String... categoriesToYeet) {
		yeet(categoriesToYeet);
	}

	public void yeet(String... categoriesToYeet) {
		for (var toYeet : categoriesToYeet) {
			categoriesRemoved.add(CategoryIdentifier.of(toYeet));
		}
	}

	public void removeIf(Predicate<CategoryConfiguration<?>> filter) {
		yeetIf(filter);
	}

	public void yeetIf(Predicate<CategoryConfiguration<?>> filter) {
		registry.stream()
				.filter(filter)
				.map(CategoryConfiguration::getIdentifier)
				.map(ResourceLocation::toString)
				.forEach(this::yeet);
	}

}