package dev.latvian.mods.kubejs.integration.jei;

import dev.latvian.mods.kubejs.event.EventResult;
import dev.latvian.mods.kubejs.recipe.viewer.AddEntriesKubeEvent;
import dev.latvian.mods.kubejs.recipe.viewer.RecipeViewerEntryType;
import dev.latvian.mods.rhino.Context;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.runtime.IJeiRuntime;

import java.util.ArrayList;
import java.util.List;

public class JEIAddEntriesKubeEvent implements AddEntriesKubeEvent {
	private final IJeiRuntime runtime;
	private final RecipeViewerEntryType type;
	private final IIngredientType ingredientType;
	private final List<Object> added;

	public JEIAddEntriesKubeEvent(IJeiRuntime r, RecipeViewerEntryType type, IIngredientType<?> t) {
		this.runtime = r;
		this.type = type;
		this.ingredientType = t;
		this.added = new ArrayList<>();
	}

	@Override
	public void add(Context cx, Object[] items) {
		for (var o : items) {
			added.add(type.wrapEntry(cx, o));
		}
	}

	@Override
	public void afterPosted(EventResult result) {
		if (!added.isEmpty()) {
			runtime.getIngredientManager().addIngredientsAtRuntime(ingredientType, added);
		}
	}
}