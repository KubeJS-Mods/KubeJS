package dev.latvian.kubejs.integration.jei;

import dev.latvian.kubejs.event.EventJS;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.runtime.IJeiRuntime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;

/**
 * @author LatvianModder
 */
public class HideJEIEventJS<T> extends EventJS
{
	private final IJeiRuntime runtime;
	private final IIngredientType<T> type;
	private final Function<Object, Collection<T>> function;
	private final Collection<T> hidden;

	public HideJEIEventJS(IJeiRuntime r, IIngredientType<T> t, Function<Object, Collection<T>> f)
	{
		runtime = r;
		type = t;
		function = f;
		hidden = new ArrayList<>();
	}

	public Collection<T> getAllIngredients()
	{
		return runtime.getIngredientManager().getAllIngredients(type);
	}

	public void hide(Object o)
	{
		hidden.addAll(function.apply(o));
	}

	public void hideAll()
	{
		hidden.addAll(getAllIngredients());
	}

	@Override
	protected void afterPosted(boolean result)
	{
		if (!hidden.isEmpty())
		{
			runtime.getIngredientManager().removeIngredientsAtRuntime(type, hidden);
		}
	}
}