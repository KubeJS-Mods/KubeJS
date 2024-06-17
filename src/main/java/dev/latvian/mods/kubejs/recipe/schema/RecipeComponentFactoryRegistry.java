package dev.latvian.mods.kubejs.recipe.schema;

import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;

public class RecipeComponentFactoryRegistry {
	private final RecipeSchemaStorage storage;

	public RecipeComponentFactoryRegistry(RecipeSchemaStorage storage) {
		this.storage = storage;
	}

	public void register(RecipeComponent<?> component) {
		storage.simpleComponents.put(component.toString(), component);
	}

	public void register(String name, RecipeComponentFactory componentFactory) {
		storage.dynamicComponents.put(name, componentFactory);
	}
}
