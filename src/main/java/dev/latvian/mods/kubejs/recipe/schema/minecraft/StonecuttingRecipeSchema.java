package dev.latvian.mods.kubejs.recipe.schema.minecraft;

import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.ItemComponents;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public interface StonecuttingRecipeSchema {
	RecipeKey<ItemStack> RESULT = ItemComponents.OUTPUT.outputKey("result");
	RecipeKey<Ingredient> INGREDIENT = ItemComponents.INPUT.inputKey("ingredient");

	RecipeSchema SCHEMA = new RecipeSchema(RESULT, INGREDIENT).uniqueOutputId(RESULT);
}
