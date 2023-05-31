package dev.latvian.mods.kubejs.recipe.schema.minecraft;

import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.item.OutputItem;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
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
		public void writeToJson(RecipeComponentValue<OutputItem> value, JsonObject json) {
			json.addProperty(value.key.name, value.value.item.kjs$getId());
			json.addProperty("count", value.value.item.getCount());
		}

		@Override
		public OutputItem readFromJson(RecipeJS recipe, RecipeKey<OutputItem> key, JsonObject json) {
			var item = RecipeComponentWithParent.super.readFromJson(recipe, key, json);

			if (item != null && json.has("count")) {
				item.item.setCount(json.get("count").getAsInt());
			}

			return item;
		}

		@Override
		public String toString() {
			return parentComponent().toString();
		}
	};

	RecipeKey<OutputItem> RESULT = RESULT_WITH_COUNT_COMPONENT.key("result");
	RecipeKey<InputItem> INGREDIENT = ItemComponents.INPUT.key("ingredient");

	RecipeSchema SCHEMA = new RecipeSchema(RESULT, INGREDIENT);
}
