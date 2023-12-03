package dev.latvian.mods.kubejs.recipe.schema;

import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;

import java.util.Map;

public class RecipeComponentFactoryRegistryEvent {
	private final Map<String, RecipeComponentFactory> map;

	public RecipeComponentFactoryRegistryEvent(Map<String, RecipeComponentFactory> map) {
		this.map = map;
	}

	public void register(String name, RecipeComponent<?> component) {
		map.put(name, new RecipeComponentFactory.Simple(component));
	}

	public void registerDynamic(String name, DynamicRecipeComponent component) {
		map.put(name, new RecipeComponentFactory.Dynamic(component));
	}
}
