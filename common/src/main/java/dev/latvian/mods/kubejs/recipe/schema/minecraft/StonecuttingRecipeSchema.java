package dev.latvian.mods.kubejs.recipe.schema.minecraft;

import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.item.OutputItem;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.ItemComponents;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;

public interface StonecuttingRecipeSchema {
	RecipeKey<OutputItem> RESULT = ItemComponents.OUTPUT.key(0, "result");
	RecipeKey<InputItem> INGREDIENT = ItemComponents.INPUT.key(1, "ingredient");

	class StonecuttingRecipeJS extends RecipeJS {
		// Override required to support custom count

		@Override
		public void deserialize() {
			setValue(INGREDIENT, INGREDIENT.component().read(json.get("ingredient")));
			var result = RESULT.component().read(json.get("result"));
			setValue(RESULT, result);

			if (json.has("count")) {
				result.item.setCount(json.get("count").getAsInt());
			}
		}

		@Override
		public void serialize() {
			if (hasChanged(INGREDIENT)) {
				json.add("ingredient", INGREDIENT.component().write(getValue(INGREDIENT)));
			}

			if (hasChanged(RESULT)) {
				var result = getValue(RESULT);
				json.addProperty("item", result.item.kjs$getId());
				json.addProperty("count", result.item.getCount());
			}
		}
	}

	RecipeSchema SCHEMA = new RecipeSchema(StonecuttingRecipeJS.class, StonecuttingRecipeJS::new, RESULT, INGREDIENT);
}
