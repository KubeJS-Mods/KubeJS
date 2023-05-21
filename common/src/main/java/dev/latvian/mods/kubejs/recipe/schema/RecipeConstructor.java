package dev.latvian.mods.kubejs.recipe.schema;

import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.recipe.RecipeFunction;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.ComponentValueMap;

public record RecipeConstructor(RecipeSchema schema, RecipeKey<?>[] keys, Factory factory) {
	@FunctionalInterface
	public interface Factory {
		Factory DEFAULT = (recipe, schemaType, map) -> {
			for (int i = 0; i < schemaType.schema.keys.length; i++) {
				recipe.setValue(schemaType.schema.keys[i], map.getValue(schemaType.schema.keys[i]));
			}
		};

		default RecipeJS create(RecipeFunction type, RecipeSchemaType schemaType, ComponentValueMap from) {
			var r = schemaType.schema.factory.get();
			r.type = type;
			r.json = new JsonObject();
			r.newRecipe = true;
			r.initValues(schemaType.schema);
			setValues(r, schemaType, from);
			r.setAllChanged(true);
			return r;
		}

		void setValues(RecipeJS recipe, RecipeSchemaType schemaType, ComponentValueMap from);
	}
}
