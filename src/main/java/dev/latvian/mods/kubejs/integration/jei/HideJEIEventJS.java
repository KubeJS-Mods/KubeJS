package dev.latvian.mods.kubejs.integration.jei;

import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.event.EventResult;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.runtime.IJeiRuntime;

import java.util.Collection;
import java.util.HashSet;
import java.util.function.Function;
import java.util.function.Predicate;

public class HideJEIEventJS<T> extends EventJS {
	private final IJeiRuntime runtime;
	private final IIngredientType<T> type;
	private final Function<Object, Predicate<T>> function;
	private final HashSet<T> hidden;
	private final Predicate<T> isValid;
	private final Collection<T> allIngredients;

	public HideJEIEventJS(IJeiRuntime r, IIngredientType<T> t, Function<Object, Predicate<T>> f, Predicate<T> i) {
		runtime = r;
		type = t;
		function = f;
		hidden = new HashSet<>();
		isValid = i;
		allIngredients = runtime.getIngredientManager().getAllIngredients(type);
	}

	public Collection<T> getAllIngredients() {
		return allIngredients;
	}

	public void hide(Object o) {
		var p = function.apply(o);

		for (var value : allIngredients) {
			if (p.test(value)) {
				hidden.add(value);
			}
		}
	}

	public void hideAll() {
		hidden.addAll(allIngredients);
	}

	@Override
	protected void afterPosted(EventResult result) {
		if (!hidden.isEmpty()) {
			runtime.getIngredientManager().removeIngredientsAtRuntime(type, hidden.stream().filter(isValid).toList());
		}
	}
}