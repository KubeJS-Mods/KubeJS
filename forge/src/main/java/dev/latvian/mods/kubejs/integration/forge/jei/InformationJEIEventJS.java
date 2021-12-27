package dev.latvian.mods.kubejs.integration.forge.jei;

import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import dev.latvian.mods.kubejs.text.Text;
import dev.latvian.mods.kubejs.util.ListJS;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.plugins.jei.info.IngredientInfoRecipe;
import net.minecraft.network.chat.Component;

import java.util.Arrays;
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

	public void add(Object o, Text[] s) {
		recipes.addAll(IngredientInfoRecipe.create(
				IngredientJS.of(o).getStacks().stream().map(ItemStackJS::getItemStack).collect(Collectors.toList()),
				VanillaTypes.ITEM,
				Arrays.stream(s).map(Text::component).toArray(Component[]::new)));
	}

	public <T> void addForType(IIngredientType<T> type, Object o, Text[] s) {
        var targets = ListJS.orSelf(o).stream().map(String::valueOf).collect(Collectors.toSet());
        var helper = manager.getIngredientHelper(type);
		recipes.addAll(IngredientInfoRecipe.create(
				manager.getAllIngredients(type)
						.stream()
						.filter(t -> targets.contains(helper.getWildcardId(t)))
						.collect(Collectors.toList()),
				type, Arrays.stream(s).map(Text::component).toArray(Component[]::new)));
	}

}