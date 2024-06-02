package dev.latvian.mods.kubejs.recipe.schema;

import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.schema.minecraft.ShapedRecipeSchema;
import dev.latvian.mods.kubejs.recipe.schema.minecraft.ShapelessRecipeSchema;
import dev.latvian.mods.kubejs.recipe.schema.minecraft.SpecialRecipeSchema;
import net.minecraft.resources.ResourceLocation;

import java.util.LinkedHashMap;

public class RecipeNamespace extends LinkedHashMap<String, RecipeSchemaType> {
	public final String name;

	public RecipeNamespace(String name) {
		this.name = name;
	}

	public RecipeNamespace register(String id, RecipeSchema type) {
		put(id, new RecipeSchemaType(this, new ResourceLocation(name, id), type));
		return this;
	}

	public RecipeNamespace registerBasic(String id, RecipeKey<?>... keys) {
		return register(id, new RecipeSchema(keys));
	}

	public RecipeNamespace shaped(String id) {
		return register(id, ShapedRecipeSchema.SCHEMA);
	}

	public RecipeNamespace shapeless(String id) {
		return register(id, ShapelessRecipeSchema.SCHEMA);
	}

	public RecipeNamespace special(String id) {
		return register(id, SpecialRecipeSchema.SCHEMA);
	}

	@Override
	public String toString() {
		return name;
	}
}
