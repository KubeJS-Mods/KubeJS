package dev.latvian.mods.kubejs.integration.forge.jei;

import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import dev.latvian.mods.kubejs.util.ListJS;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.network.chat.Component;

import java.util.HashSet;

/**
 * @author LatvianModder
 */

public class InformationJEIEventJS extends EventJS {
	private final IRecipeRegistration registration;

	public InformationJEIEventJS(IRecipeRegistration reg) {
		registration = reg;
	}

	public void add(Object o, Component[] s) {
		registration.addIngredientInfo(
				IngredientJS.of(o).getStacks().stream().map(ItemStackJS::getItemStack).toList(),
				VanillaTypes.ITEM_STACK,
				s
		);
	}

	public <T> void addForType(IIngredientType<T> type, Object o, Component[] s) {
		var targets = new HashSet<>(ListJS.orSelf(o).map(String::valueOf));
		var manager = registration.getIngredientManager();
		var helper = manager.getIngredientHelper(type);
		registration.addIngredientInfo(
				manager.getAllIngredients(type)
						.stream()
						.filter(t -> targets.contains(helper.getWildcardId(t)))
						.toList(),
				type, s);
	}

}