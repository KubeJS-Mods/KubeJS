package dev.latvian.mods.kubejs.recipe.schema.minecraft;

import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.item.OutputItem;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.ItemComponents;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;

public interface ShapelessRecipeSchema {
	RecipeKey<OutputItem> RESULT = ItemComponents.OUTPUT.key(0, "result");
	RecipeKey<InputItem[]> INGREDIENTS = ItemComponents.UNWRAPPED_INPUT_ARRAY.key(1, "ingredients");

	RecipeSchema SCHEMA = new RecipeSchema(RESULT, INGREDIENTS);
}
