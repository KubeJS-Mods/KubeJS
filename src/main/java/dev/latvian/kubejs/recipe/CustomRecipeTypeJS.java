package dev.latvian.kubejs.recipe;

import dev.latvian.kubejs.recipe.minecraft.CustomRecipeJS;
import net.minecraft.item.crafting.IRecipeSerializer;

/**
 * @author LatvianModder
 */
public class CustomRecipeTypeJS extends RecipeTypeJS
{
	public CustomRecipeTypeJS(IRecipeSerializer<?> s)
	{
		super(s, CustomRecipeJS::new);
	}

	@Override
	public boolean isCustom()
	{
		return true;
	}
}