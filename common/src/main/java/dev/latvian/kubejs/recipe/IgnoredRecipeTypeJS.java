package dev.latvian.kubejs.recipe;

import net.minecraft.world.item.crafting.RecipeSerializer;

/**
 * @author LatvianModder
 */
public class IgnoredRecipeTypeJS extends RecipeTypeJS
{
	public IgnoredRecipeTypeJS(RecipeSerializer<?> s)
	{
		super(s, IgnoredRecipeJS::new);
	}
}