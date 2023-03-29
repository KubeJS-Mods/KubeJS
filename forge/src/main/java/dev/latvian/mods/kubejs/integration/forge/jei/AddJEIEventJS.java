package dev.latvian.mods.kubejs.integration.forge.jei;

import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.event.EventResult;
import dev.latvian.mods.kubejs.util.ListJS;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.runtime.IJeiRuntime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author LatvianModder
 */
public class AddJEIEventJS<T> extends EventJS {
	private final IJeiRuntime runtime;
	private final IIngredientType<T> type;
	private final Function<Object, T> function;
	private final Collection<T> added;
	private final Predicate<T> isValid;

	public AddJEIEventJS(IJeiRuntime r, IIngredientType<T> t, Function<Object, T> f, Predicate<T> i) {
		runtime = r;
		type = t;
		function = f;
		added = new ArrayList<>();
		isValid = i;
	}

	public void add(Object o) {
		for (var o1 : ListJS.orSelf(o)) {
			var t = function.apply(o1);

			if (t != null) {
				added.add(t);
			}
		}
	}

	@Override
	protected void afterPosted(EventResult result) {
		if (!added.isEmpty()) {
			var items = added.stream().filter(isValid).collect(Collectors.toList());
			runtime.getIngredientManager().addIngredientsAtRuntime(type, items);
		}
	}
}