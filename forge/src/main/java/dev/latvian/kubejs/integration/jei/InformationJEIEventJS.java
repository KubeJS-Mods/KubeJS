package dev.latvian.kubejs.integration.jei;

import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.util.ListJS;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.plugins.jei.info.IngredientInfoRecipe;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author LatvianModder
 */
public class InformationJEIEventJS extends EventJS {
	private final IIngredientManager manager;
	private final List<IngredientInfoRecipe<?>> recipes;

	public InformationJEIEventJS(IIngredientManager m, List<IngredientInfoRecipe<?>> l) {
		manager = m;
		recipes = l;
	}

	public void add(Object o, Object s) {
		recipes.addAll(IngredientInfoRecipe.create(
				IngredientJS.of(o).getStacks().stream().map(ItemStackJS::getItemStack).collect(Collectors.toList()),
				VanillaTypes.ITEM,
				ListJS.orSelf(s).stream().map(String::valueOf).toArray(String[]::new)));
	}

	public <T> void addForType(IIngredientType<T> type, Object o, Object s) {
		Set<String> targets = ListJS.orSelf(o).stream().map(String::valueOf).collect(Collectors.toSet());
		IIngredientHelper<T> helper = manager.getIngredientHelper(type);
		recipes.addAll(IngredientInfoRecipe.create(
				manager.getAllIngredients(type)
						.stream()
						.filter(t -> targets.contains(helper.getWildcardId(t)))
						.collect(Collectors.toList()),
				type, ListJS.orSelf(s).stream().map(String::valueOf).toArray(String[]::new)));
	}

}