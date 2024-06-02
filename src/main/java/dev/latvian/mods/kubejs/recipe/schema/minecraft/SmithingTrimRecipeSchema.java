package dev.latvian.mods.kubejs.recipe.schema.minecraft;

import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.ItemComponents;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import net.minecraft.world.item.crafting.Ingredient;

public interface SmithingTrimRecipeSchema {
	RecipeKey<Ingredient> TEMPLATE = ItemComponents.INPUT.inputKey("template");
	RecipeKey<Ingredient> BASE = ItemComponents.INPUT.inputKey("base");
	RecipeKey<Ingredient> ADDITION = ItemComponents.INPUT.inputKey("addition");

	RecipeSchema SCHEMA = new RecipeSchema(TEMPLATE, BASE, ADDITION).uniqueInputId(BASE);
}
