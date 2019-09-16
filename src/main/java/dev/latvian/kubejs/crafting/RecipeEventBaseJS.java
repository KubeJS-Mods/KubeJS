package dev.latvian.kubejs.crafting;

import dev.latvian.kubejs.event.EventJS;

import java.util.Map;

/**
 * @author LatvianModder
 */
public abstract class RecipeEventBaseJS<T extends RecipeJS> extends EventJS
{
	private final String mod;

	public RecipeEventBaseJS(String m)
	{
		mod = m;
	}

	public String getMod()
	{
		return mod;
	}

	protected abstract T createRecipe();

	public final void add(Map<String, Object> properties)
	{
		T recipe = createRecipe();
		recipe.set(properties);
		recipe.add();
	}

	public void remove(Object output)
	{
	}

	public void removeInput(Object input)
	{
	}
}