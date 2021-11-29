package dev.latvian.mods.kubejs.recipe;

import dev.latvian.mods.kubejs.recipe.minecraft.CustomRecipeJS;
import net.minecraft.world.item.crafting.RecipeSerializer;

/**
 * @author LatvianModder
 */
public class CustomRecipeTypeJS extends RecipeTypeJS {
	public CustomRecipeTypeJS(RecipeSerializer<?> s) {
		super(s, CustomRecipeJS::new);
	}

	@Override
	public boolean isCustom() {
		return true;
	}
}