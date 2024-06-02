package dev.latvian.mods.kubejs.recipe.schema.minecraft;

import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.RecipeTypeFunction;
import dev.latvian.mods.kubejs.recipe.component.ItemComponents;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

public interface ShapelessRecipeSchema {
	class ShapelessKubeRecipe extends KubeRecipe {
		@Override
		public RecipeTypeFunction getSerializationTypeFunction() {
			// Use vanilla shapeless recipe type if KubeJS is not needed
			if (type == type.event.shapeless // if this type == kubejs:shapeless
				&& type.event.shapeless != type.event.vanillaShapeless // check if not in serverOnly mode
				&& !json.has("kubejs:actions")
				&& !json.has("kubejs:modify_result")
				&& !json.has("kubejs:stage")
			) {
				return type.event.vanillaShapeless;
			}

			return super.getSerializationTypeFunction();
		}
	}

	RecipeKey<ItemStack> RESULT = ItemComponents.OUTPUT.outputKey("result");
	RecipeKey<List<Ingredient>> INGREDIENTS = ItemComponents.UNWRAPPED_INPUT_LIST.inputKey("ingredients");

	RecipeSchema SCHEMA = new RecipeSchema(ShapelessKubeRecipe.class, ShapelessKubeRecipe::new, RESULT, INGREDIENTS).uniqueOutputId(RESULT);
}
