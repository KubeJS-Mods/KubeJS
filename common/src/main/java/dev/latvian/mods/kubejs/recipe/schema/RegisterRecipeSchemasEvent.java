package dev.latvian.mods.kubejs.recipe.schema;

import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public record RegisterRecipeSchemasEvent(Map<String, RecipeNamespace> namespaces, Map<String, ResourceLocation> mappedRecipes) {
	public RecipeNamespace namespace(String namespace) {
		return namespaces.computeIfAbsent(namespace, RecipeNamespace::new);
	}

	public void register(ResourceLocation id, RecipeSchema schema) {
		namespace(id.getNamespace()).register(id.getPath(), schema);
	}

	public void mapRecipe(String name, ResourceLocation type) {
		mappedRecipes.put(name, type);
	}

	public void mapRecipe(String name, String type) {
		mapRecipe(name, new ResourceLocation(type));
	}
}