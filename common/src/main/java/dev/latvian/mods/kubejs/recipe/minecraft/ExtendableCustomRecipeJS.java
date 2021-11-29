package dev.latvian.mods.kubejs.recipe.minecraft;

import dev.latvian.mods.kubejs.util.ListJS;

public abstract class ExtendableCustomRecipeJS extends CustomRecipeJS {
	@Override
	public abstract void create(ListJS args);
}
