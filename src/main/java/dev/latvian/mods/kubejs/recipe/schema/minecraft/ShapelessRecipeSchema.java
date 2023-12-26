package dev.latvian.mods.kubejs.recipe.schema.minecraft;

import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.item.OutputItem;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.RecipeTypeFunction;
import dev.latvian.mods.kubejs.recipe.component.ItemComponents;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;

public interface ShapelessRecipeSchema {
	class ShapelessRecipeJS extends RecipeJS {
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

	RecipeKey<OutputItem> RESULT = ItemComponents.OUTPUT.key("result");
	RecipeKey<InputItem[]> INGREDIENTS = ItemComponents.UNWRAPPED_INPUT_ARRAY.key("ingredients");

	RecipeSchema SCHEMA = new RecipeSchema(ShapelessRecipeJS.class, ShapelessRecipeJS::new, RESULT, INGREDIENTS).uniqueOutputId(RESULT);
}
