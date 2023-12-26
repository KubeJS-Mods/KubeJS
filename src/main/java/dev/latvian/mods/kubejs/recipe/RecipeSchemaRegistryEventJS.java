package dev.latvian.mods.kubejs.recipe;

import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.recipe.schema.RecipeComponentFactory;
import dev.latvian.mods.kubejs.recipe.schema.RecipeComponentFactoryRegistryEvent;
import dev.latvian.mods.kubejs.recipe.schema.RecipeNamespace;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import dev.latvian.mods.kubejs.util.KubeJSPlugins;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class RecipeSchemaRegistryEventJS extends EventJS {
	private final Map<String, RecipeNamespace> namespaces;
	private final Map<String, ResourceLocation> mappedRecipes;
	private Map<String, RecipeComponentFactory> components;

	public RecipeSchemaRegistryEventJS(Map<String, RecipeNamespace> namespaces, Map<String, ResourceLocation> mappedRecipes) {
		this.namespaces = namespaces;
		this.mappedRecipes = mappedRecipes;
	}

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

	public Map<String, RecipeComponentFactory> getComponents() {
		if (components == null) {
			components = new HashMap<>();
			KubeJSPlugins.forEachPlugin(new RecipeComponentFactoryRegistryEvent(components), KubeJSPlugin::registerRecipeComponents);
		}

		return components;
	}
}