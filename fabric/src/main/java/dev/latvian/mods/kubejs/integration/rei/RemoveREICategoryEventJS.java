package dev.latvian.mods.kubejs.integration.rei;

import dev.latvian.mods.kubejs.event.EventJS;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.CollectionUtils;

import java.util.Collection;
import java.util.Set;

public class RemoveREICategoryEventJS extends EventJS {
	private final Set<CategoryIdentifier<?>> categoriesRemoved;

	public RemoveREICategoryEventJS(Set<CategoryIdentifier<?>> categoriesRemoved) {
		this.categoriesRemoved = categoriesRemoved;
	}

	public Collection<String> getCategories() {
		return CollectionUtils.map(CategoryRegistry.getInstance(), category -> category.getIdentifier().toString());
	}

	public void yeet(String categoryToYeet) {
		yeet(new String[]{categoryToYeet});
	}

	public void yeet(String[] categoriesToYeet) {
		for (String toYeet : categoriesToYeet) {
			categoriesRemoved.add(CategoryIdentifier.of(toYeet));
		}
	}
}