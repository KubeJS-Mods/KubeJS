package dev.latvian.kubejs.recipe;

/**
 * @author LatvianModder
 */
public interface IRecipeCollection
{
	void remove();

	int getCount();

	boolean hasInput(Object ingredient);

	boolean replaceInput(Object ingredient, Object with);

	boolean hasOutput(Object ingredient);

	boolean replaceOutput(Object ingredient, Object with);

	void setGroup(String group);
}