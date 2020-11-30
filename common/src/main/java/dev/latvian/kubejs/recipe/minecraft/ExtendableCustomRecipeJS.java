package dev.latvian.kubejs.recipe.minecraft;

import dev.latvian.kubejs.util.ListJS;

public abstract class ExtendableCustomRecipeJS extends CustomRecipeJS
{
	@Override
	public abstract void create(ListJS args);
}
