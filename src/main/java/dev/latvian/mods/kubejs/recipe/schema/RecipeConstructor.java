package dev.latvian.mods.kubejs.recipe.schema;

import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.RecipeTypeFunction;
import dev.latvian.mods.kubejs.recipe.component.ComponentValueMap;
import dev.latvian.mods.kubejs.util.Cast;
import dev.latvian.mods.rhino.Context;

import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public record RecipeConstructor(RecipeSchema schema, RecipeKey<?>[] keys, Factory factory) {
	@FunctionalInterface
	public interface Factory {
		Factory DEFAULT = (recipe, schemaType, keys, from) -> {
			for (var key : keys) {
				recipe.setValue(key, Cast.to(from.getValue(recipe, key)));
			}
		};

		static Factory defaultWith(BiFunction<KubeRecipe, RecipeKey<?>, Object> valueSupplier) {
			return (recipe, schemaType, keys, from) -> {
				DEFAULT.setValues(recipe, schemaType, keys, from);

				for (var key : schemaType.schema.keys) {
					var v = valueSupplier.apply(recipe, key);

					if (v != null) {
						recipe.setValue(key, Cast.to(v));
					}
				}
			};
		}

		default KubeRecipe create(Context cx, RecipeTypeFunction type, RecipeSchemaType schemaType, RecipeKey<?>[] keys, ComponentValueMap from) {
			var r = schemaType.schema.factory.get();
			r.type = type;
			r.json = new JsonObject();
			r.json.addProperty("type", "unknown");
			r.newRecipe = true;
			r.initValues(true);
			setValues(r, schemaType, keys, from);
			return r;
		}

		void setValues(KubeRecipe recipe, RecipeSchemaType schemaType, RecipeKey<?>[] keys, ComponentValueMap from);
	}

	@Override
	public String toString() {
		return Arrays.stream(keys).map(RecipeKey::toString).collect(Collectors.joining(", ", "(", ")"));
	}
}
