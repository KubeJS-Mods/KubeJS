package dev.latvian.mods.kubejs.recipe.schema.minecraft;

import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.item.OutputItem;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.ItemComponents;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentValue;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentWithParent;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;

public interface StonecuttingRecipeSchema {
	RecipeComponent<OutputItem> RESULT_WITH_COUNT_COMPONENT = new RecipeComponentWithParent<>() {
		@Override
		public RecipeComponent<OutputItem> parentComponent() {
			return ItemComponents.OUTPUT;
		}

		@Override
		public void writeJson(RecipeComponentValue<OutputItem> value, JsonObject json) {
			json.addProperty(value.key.name(), value.value.item.kjs$getId());
			json.addProperty("count", value.value.item.getCount());
		}

		@Override
		public void readJson(RecipeComponentValue<OutputItem> value, JsonObject json) {
			value.value = ItemComponents.OUTPUT.read(json.get(value.key.name()));

			if (json.has("count")) {
				value.value.item.setCount(json.get("count").getAsInt());
			}
		}
	};

	RecipeKey<OutputItem> RESULT = RESULT_WITH_COUNT_COMPONENT.key(0, "result");
	RecipeKey<InputItem> INGREDIENT = ItemComponents.INPUT.key(1, "ingredient");

	RecipeSchema SCHEMA = new RecipeSchema(RESULT, INGREDIENT);
}
