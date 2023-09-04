package dev.latvian.mods.kubejs.recipe.schema.minecraft;

import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.ItemComponents;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;

public interface SmithingTrimRecipeSchema {
	RecipeKey<InputItem> TEMPLATE = ItemComponents.INPUT.key("template");
	RecipeKey<InputItem> BASE = ItemComponents.INPUT.key("base");
	RecipeKey<InputItem> ADDITION = ItemComponents.INPUT.key("addition");

	RecipeSchema SCHEMA = new RecipeSchema(TEMPLATE, BASE, ADDITION).uniqueInputId(BASE);
}
