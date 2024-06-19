package dev.latvian.mods.kubejs.integration.jei;

import dev.latvian.mods.kubejs.event.EventResult;
import dev.latvian.mods.kubejs.recipe.viewer.RecipeViewerEntryType;
import dev.latvian.mods.kubejs.recipe.viewer.RemoveEntriesKubeEvent;
import dev.latvian.mods.rhino.Context;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.runtime.IJeiRuntime;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.function.Predicate;

public class JEIRemoveEntriesKubeEvent implements RemoveEntriesKubeEvent {
	private final IJeiRuntime runtime;
	private final RecipeViewerEntryType type;
	private final IIngredientType ingredientType;
	private final Collection<Object> hidden;
	private final Object[] allIngredients;

	public JEIRemoveEntriesKubeEvent(IJeiRuntime r, RecipeViewerEntryType type, IIngredientType<?> t) {
		this.runtime = r;
		this.type = type;
		this.ingredientType = t;
		this.hidden = new HashSet<>();
		this.allIngredients = runtime.getIngredientManager().getAllIngredients(ingredientType).toArray();
	}

	@Override
	public Object[] getAllEntryValues() {
		return allIngredients;
	}

	@Override
	public void remove(Context cx, Object filter) {
		var predicate = (Predicate) type.wrapPredicate(cx, filter);

		for (var value : allIngredients) {
			if (predicate.test(value)) {
				hidden.add(value);
			}
		}
	}

	@Override
	public void removeDirectly(Context cx, Object filter) {
	}

	@Override
	public void removeAll() {
		hidden.addAll(List.of(allIngredients));
	}

	@Override
	public void afterPosted(EventResult result) {
		if (!hidden.isEmpty()) {
			runtime.getIngredientManager().removeIngredientsAtRuntime(ingredientType, hidden);
		}
	}
}