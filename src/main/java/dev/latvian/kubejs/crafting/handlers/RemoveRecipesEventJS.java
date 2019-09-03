package dev.latvian.kubejs.crafting.handlers;

import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;

import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
public class RemoveRecipesEventJS extends EventJS
{
	public final String mod;
	public final String type;
	private final Consumer<IngredientJS> callback;

	public RemoveRecipesEventJS(String m, String t, Consumer<IngredientJS> c)
	{
		mod = m;
		type = t;
		callback = c;
	}

	public void remove(Object output)
	{
		IngredientJS ingredient = IngredientJS.of(output);

		if (!ingredient.isEmpty())
		{
			callback.accept(ingredient);
		}
	}
}