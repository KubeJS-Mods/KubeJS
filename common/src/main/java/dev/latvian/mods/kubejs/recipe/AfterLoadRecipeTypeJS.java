package dev.latvian.mods.kubejs.recipe;

import dev.latvian.mods.kubejs.recipe.minecraft.CustomRecipeJS;
import net.minecraft.world.item.crafting.RecipeSerializer;

/**
 * @author LatvianModder
 */
public class AfterLoadRecipeTypeJS extends RecipeTypeJS {
	public AfterLoadRecipeTypeJS(RecipeSerializer<?> s) {
		super(s, CustomRecipeJS::new);
	}
}