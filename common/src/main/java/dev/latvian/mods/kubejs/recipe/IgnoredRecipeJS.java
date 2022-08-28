package dev.latvian.mods.kubejs.recipe;

/**
 * @author LatvianModder
 */
public class IgnoredRecipeJS extends JsonRecipeJS {
	@Override
	public void create(RecipeArguments args) {
		throw new RecipeExceptionJS("Can't create an ignored recipe!");
	}
}