package dev.latvian.mods.kubejs.recipe.schema.minecraft;

import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.item.OutputItem;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.ItemComponents;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;

public interface SmithingTransformRecipeSchema {
	RecipeKey<OutputItem> RESULT = ItemComponents.OUTPUT.key("result");
	RecipeKey<InputItem> TEMPLATE = ItemComponents.INPUT.key("template");
	RecipeKey<InputItem> BASE = ItemComponents.INPUT.key("base");
	RecipeKey<InputItem> ADDITION = ItemComponents.INPUT.key("addition");

	RecipeSchema SCHEMA = new RecipeSchema(RESULT, TEMPLATE, BASE, ADDITION).uniqueOutputId(RESULT);
}
