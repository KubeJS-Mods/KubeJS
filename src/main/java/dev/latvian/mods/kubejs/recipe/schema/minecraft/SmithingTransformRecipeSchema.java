package dev.latvian.mods.kubejs.recipe.schema.minecraft;

import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.IngredientComponent;
import dev.latvian.mods.kubejs.recipe.component.ItemStackComponent;
import dev.latvian.mods.kubejs.recipe.schema.RecipeConstructor;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

public interface SmithingTransformRecipeSchema {
	RecipeKey<ItemStack> RESULT = ItemStackComponent.ITEM_STACK.outputKey("result");
	RecipeKey<Ingredient> TEMPLATE = IngredientComponent.INGREDIENT.inputKey("template");
	RecipeKey<Ingredient> BASE = IngredientComponent.INGREDIENT.inputKey("base");
	RecipeKey<Ingredient> ADDITION = IngredientComponent.INGREDIENT.inputKey("addition");

	RecipeSchema SCHEMA = new RecipeSchema(RESULT, TEMPLATE, BASE, ADDITION)
		.uniqueId(RESULT)
		.constructor(RESULT, TEMPLATE, BASE, ADDITION)
		.constructor(RecipeConstructor.Factory.defaultWith((recipe, key) -> {
			if (key == TEMPLATE) {
				return InputItem.create(Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE), 1);
			} else {
				return null;
			}
		}), RESULT, BASE, ADDITION);
}
