package dev.latvian.mods.kubejs.recipe.schema.minecraft;

import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.item.OutputItem;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.ItemComponents;
import dev.latvian.mods.kubejs.recipe.schema.RecipeConstructor;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

public interface SmithingTransformRecipeSchema {
	RecipeKey<OutputItem> RESULT = ItemComponents.OUTPUT.key("result");
	RecipeKey<InputItem> TEMPLATE = ItemComponents.INPUT.key("template");
	RecipeKey<InputItem> BASE = ItemComponents.INPUT.key("base");
	RecipeKey<InputItem> ADDITION = ItemComponents.INPUT.key("addition");

	RecipeSchema SCHEMA = new RecipeSchema(RESULT, TEMPLATE, BASE, ADDITION)
		.uniqueOutputId(RESULT)
		.constructor(RESULT, TEMPLATE, BASE, ADDITION)
		.constructor(RecipeConstructor.Factory.defaultWith((recipe, key) -> {
			if (key == TEMPLATE) {
				return InputItem.of(Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE), 1);
			} else {
				return null;
			}
		}), RESULT, BASE, ADDITION);
}
