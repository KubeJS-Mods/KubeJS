package dev.latvian.mods.kubejs.recipe.schema;

import dev.latvian.mods.kubejs.recipe.RecipeKey;
import net.minecraft.resources.ResourceLocation;

import java.util.LinkedHashMap;

public class RecipeNamespace extends LinkedHashMap<String, RecipeSchemaType> {
	public final RecipeSchemaStorage storage;
	public final String name;

	public RecipeNamespace(RecipeSchemaStorage storage, String name) {
		this.storage = storage;
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
		return register(id, storage.shapedSchema);
	}

	public RecipeNamespace shapeless(String id) {
		return register(id, storage.shapelessSchema);
	}

	public RecipeNamespace special(String id) {
		return register(id, storage.specialSchema);
	}

	@Override
	public String toString() {
		return name;
	}
}
