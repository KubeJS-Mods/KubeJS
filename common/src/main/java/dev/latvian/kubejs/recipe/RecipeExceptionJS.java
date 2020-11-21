package dev.latvian.kubejs.recipe;

/**
 * @author LatvianModder
 */
public class RecipeExceptionJS extends IllegalArgumentException
{
	public boolean fallback;

	public RecipeExceptionJS(String m)
	{
		super(m);
		fallback = false;
	}

	@Override
	public String toString()
	{
		return getLocalizedMessage();
	}

	public RecipeExceptionJS fallback()
	{
		fallback = true;
		return this;
	}
}