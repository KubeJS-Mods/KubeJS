package dev.latvian.mods.kubejs.recipe;

import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.recipe.schema.RecipeNamespace;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class RecipeSchemaRegistryEventJS extends EventJS {
	private final Map<String, RecipeNamespace> namespaces;
	private final Map<String, ResourceLocation> mappedRecipes;

	public RecipeSchemaRegistryEventJS(Map<String, RecipeNamespace> namespaces, Map<String, ResourceLocation> mappedRecipes) {
		this.namespaces = namespaces;
		this.mappedRecipes = mappedRecipes;
	}

	public RecipeNamespace namespace(String namespace) {
		return namespaces.computeIfAbsent(namespace, RecipeNamespace::new);
	}

	public void mapRecipe(String name, ResourceLocation type) {
		mappedRecipes.put(name, type);
	}

	public void mapRecipe(String name, String type) {
		mapRecipe(name, new ResourceLocation(type));
	}
}