package dev.latvian.mods.kubejs.recipe.schema;

import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;

public class RecipeComponentFactoryRegistryEvent {
	private final RecipeSchemaStorage storage;

	public RecipeComponentFactoryRegistryEvent(RecipeSchemaStorage storage) {
		this.storage = storage;
	}

	public void register(String name, RecipeComponent<?> component) {
		storage.simpleComponents.put(name, component);
	}

	public void register(String name, RecipeComponentFactory componentFactory) {
		storage.dynamicComponents.put(name, componentFactory);
	}
}
