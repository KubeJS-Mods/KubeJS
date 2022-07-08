package dev.latvian.mods.kubejs.recipe;

/**
 * @author LatvianModder
 */
public class IgnoredRecipeJS extends RecipeJS {
	@Override
	public void create(RecipeArguments args) {
		throw new RecipeExceptionJS("Can't create an ignored recipe!");
	}

	@Override
	public void deserialize() {
	}

	@Override
	public void serialize() {
	}
}