package dev.latvian.kubejs.recipe;

import dev.latvian.kubejs.script.data.VirtualKubeJSDataPack;

/**
 * @author LatvianModder
 */
public interface IRecipeCollection
{
	void remove();

	boolean hasInput(Object ingredient);

	boolean hasOutput(Object ingredient);

	boolean replaceInput(Object ingredient, Object with);

	boolean replaceOutput(Object ingredient, Object with);

	void setGroup(String group);

	void addToDataPack(VirtualKubeJSDataPack pack);
}