package dev.latvian.mods.kubejs.recipe.schema;

import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.RecipeTypeFunction;
import dev.latvian.mods.kubejs.recipe.component.ComponentValueMap;

import java.util.Arrays;
import java.util.stream.Collectors;

public record RecipeConstructor(RecipeSchema schema, RecipeKey<?>[] keys, Factory factory) {
	@FunctionalInterface
	public interface Factory {
		Factory DEFAULT = (recipe, schemaType, keys, from) -> {
			for (var key : keys) {
				recipe.setValue(key, from.getValue(recipe, key));
			}
		};

		default RecipeJS create(RecipeTypeFunction type, RecipeSchemaType schemaType, RecipeKey<?>[] keys, ComponentValueMap from) {
			var r = schemaType.schema.factory.get();
			r.type = type;
			r.json = new JsonObject();
			r.newRecipe = true;
			r.initValues(true);
			setValues(r, schemaType, keys, from);
			return r;
		}

		void setValues(RecipeJS recipe, RecipeSchemaType schemaType, RecipeKey<?>[] keys, ComponentValueMap from);
	}

	@Override
	public String toString() {
		return Arrays.stream(keys).map(RecipeKey::toString).collect(Collectors.joining(", ", "(", ")"));
	}
}
