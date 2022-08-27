package dev.latvian.mods.kubejs.recipe;

import dev.latvian.mods.kubejs.recipe.minecraft.JsonRecipeJS;
import net.minecraft.world.item.crafting.RecipeSerializer;

/**
 * @author LatvianModder
 */
public class JsonRecipeTypeJS extends RecipeTypeJS {
	public JsonRecipeTypeJS(RecipeSerializer<?> s) {
		super(s, JsonRecipeJS::new);
	}

	@Override
	public boolean isCustom() {
		return true;
	}
}