package dev.latvian.mods.kubejs.recipe.schema.minecraft;

import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.item.OutputItem;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;

public interface SmithingRecipeSchema {
	RecipeKey<OutputItem> RESULT = RecipeSchema.OUTPUT_ITEM.key(0, "result");
	RecipeKey<InputItem> BASE = RecipeSchema.INPUT_ITEM.key(1, "base");
	RecipeKey<InputItem> ADDITION = RecipeSchema.INPUT_ITEM.key(2, "addition");

	RecipeSchema SCHEMA = new RecipeSchema(RESULT, BASE, ADDITION);
}
