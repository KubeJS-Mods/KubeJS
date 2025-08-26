package dev.latvian.mods.kubejs.recipe.schema.minecraft;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.RecipeTypeFunction;
import dev.latvian.mods.kubejs.recipe.schema.KubeRecipeFactory;
import dev.latvian.mods.kubejs.recipe.special.KubeJSCraftingRecipe;

public class ShapedKubeRecipe extends KubeRecipe {
	public static final KubeRecipeFactory RECIPE_FACTORY = new KubeRecipeFactory(KubeJS.id("shaped"), ShapedKubeRecipe.class, ShapedKubeRecipe::new);

	@Override
	public RecipeTypeFunction getSerializationTypeFunction() {
		// Use vanilla shaped recipe type if KubeJS is not needed
		if (type == type.event.shaped // if this type == kubejs:shaped
			&& type.event.shaped != type.event.vanillaShaped // check if not in serverOnly mode
			&& !json.has(KubeJSCraftingRecipe.INGREDIENT_ACTIONS_KEY)
			&& !json.has(KubeJSCraftingRecipe.MODIFY_RESULT_KEY)
			&& !json.has(KubeJSCraftingRecipe.STAGE_KEY)
			&& !json.has(KubeJSCraftingRecipe.MIRROR_KEY)
		) {
			return type.event.vanillaShaped;
		}

		return super.getSerializationTypeFunction();
	}
}
