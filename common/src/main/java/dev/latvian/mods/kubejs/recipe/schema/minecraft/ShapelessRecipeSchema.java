package dev.latvian.mods.kubejs.recipe.schema.minecraft;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.item.OutputItem;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentWithParent;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;

import java.util.List;

public interface ShapelessRecipeSchema {
	RecipeComponent<List<InputItem>> UNWRAPPED_INPUT_ITEM_ARRAY = new RecipeComponentWithParent<>() {
		@Override
		public RecipeComponent<List<InputItem>> parentComponent() {
			return RecipeSchema.INPUT_ITEM_ARRAY;
		}

		@Override
		public JsonElement write(List<InputItem> value) {
			var json = new JsonArray();

			for (var in : value) {
				for (var in1 : in.unwrap()) {
					json.add(RecipeSchema.INPUT_ITEM.write(in1));
				}
			}

			return json;
		}
	};

	RecipeKey<OutputItem> RESULT = RecipeSchema.OUTPUT_ITEM.key(0, "result");
	RecipeKey<List<InputItem>> INGREDIENTS = UNWRAPPED_INPUT_ITEM_ARRAY.key(1, "ingredients");

	RecipeSchema SCHEMA = new RecipeSchema(RESULT, INGREDIENTS);
}
