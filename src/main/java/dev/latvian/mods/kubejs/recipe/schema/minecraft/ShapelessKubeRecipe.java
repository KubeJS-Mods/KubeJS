package dev.latvian.mods.kubejs.recipe.schema.minecraft;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.RecipeTypeFunction;
import dev.latvian.mods.kubejs.recipe.schema.KubeRecipeFactory;

public class ShapelessKubeRecipe extends KubeRecipe {
	public static final KubeRecipeFactory RECIPE_FACTORY = new KubeRecipeFactory(KubeJS.id("shapeless"), ShapelessKubeRecipe.class, ShapelessKubeRecipe::new);

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
