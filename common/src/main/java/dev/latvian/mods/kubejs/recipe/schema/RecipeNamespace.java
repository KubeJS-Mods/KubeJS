package dev.latvian.mods.kubejs.recipe.schema;

import dev.latvian.mods.kubejs.KubeJSRegistries;
import dev.latvian.mods.kubejs.bindings.event.StartupEvents;
import dev.latvian.mods.kubejs.recipe.RecipeSchemaRegistryEventJS;
import dev.latvian.mods.kubejs.recipe.schema.minecraft.ShapedRecipeSchema;
import dev.latvian.mods.kubejs.recipe.schema.minecraft.ShapelessRecipeSchema;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.util.KubeJSPlugins;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class RecipeNamespace extends LinkedHashMap<String, RecipeSchemaType> {
	private static Map<String, ResourceLocation> mappedRecipes;
	private static Map<String, RecipeNamespace> all;

	public static Map<String, RecipeNamespace> getAll() {
		if (all == null) {
			all = new HashMap<>();
			mappedRecipes = new HashMap<>();

			for (var entry : KubeJSRegistries.recipeSerializers().entrySet()) {
				var ns = all.computeIfAbsent(entry.getKey().location().getNamespace(), RecipeNamespace::new);
				ns.put(entry.getKey().location().getPath(), new JsonRecipeSchemaType(ns, entry.getKey().location(), entry.getValue()));
			}

			var event = new RegisterRecipeSchemasEvent(all, mappedRecipes);
			KubeJSPlugins.forEachPlugin(plugin -> plugin.registerRecipeSchemas(event));
			StartupEvents.RECIPE_SCHEMA_REGISTRY.post(ScriptType.STARTUP, new RecipeSchemaRegistryEventJS(all, mappedRecipes));
		}

		return all;
	}

	public static Map<String, ResourceLocation> getMappedRecipes() {
		getAll();
		return mappedRecipes;
	}

	public final String name;

	public RecipeNamespace(String name) {
		this.name = name;
	}

	public RecipeNamespace register(String id, RecipeSchema type) {
		put(id, new RecipeSchemaType(this, new ResourceLocation(name, id), type));
		return this;
	}

	public RecipeNamespace shaped(String id) {
		return register(id, ShapedRecipeSchema.SCHEMA);
	}

	public RecipeNamespace shapeless(String id) {
		return register(id, ShapelessRecipeSchema.SCHEMA);
	}

	@Override
	public String toString() {
		return name;
	}
}
