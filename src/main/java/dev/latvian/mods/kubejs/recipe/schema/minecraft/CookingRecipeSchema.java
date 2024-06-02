package dev.latvian.mods.kubejs.recipe.schema.minecraft;

import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.ItemComponents;
import dev.latvian.mods.kubejs.recipe.component.NumberComponent;
import dev.latvian.mods.kubejs.recipe.component.TimeComponent;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import dev.latvian.mods.kubejs.util.TickDuration;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public interface CookingRecipeSchema {
	RecipeKey<ItemStack> RESULT = ItemComponents.OUTPUT.outputKey("result");
	RecipeKey<Ingredient> INGREDIENT = ItemComponents.INPUT.inputKey("ingredient");
	RecipeKey<Float> XP = NumberComponent.FLOAT.outputKey("experience").optional(0F).preferred("xp");
	RecipeKey<TickDuration> COOKING_TIME = TimeComponent.TICKS.inputKey("cookingtime").optional(new TickDuration(200L)).preferred("cookingTime");

	RecipeSchema SCHEMA = new RecipeSchema(RESULT, INGREDIENT, XP, COOKING_TIME).uniqueOutputId(RESULT);
}
