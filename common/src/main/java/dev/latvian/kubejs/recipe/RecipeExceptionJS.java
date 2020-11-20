package dev.latvian.kubejs.recipe;

/**
 * @author LatvianModder
 */
public class RecipeExceptionJS extends IllegalArgumentException
{
	public RecipeExceptionJS(String m)
	{
		super(m);
	}

	@Override
	public String toString()
	{
		return getLocalizedMessage();
	}
}