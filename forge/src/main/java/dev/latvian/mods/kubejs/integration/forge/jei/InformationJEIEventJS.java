package dev.latvian.mods.kubejs.integration.forge.jei;

import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import dev.latvian.mods.kubejs.text.Text;
import dev.latvian.mods.kubejs.util.ListJS;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.Util;
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

	public void add(Object o, Text[] s) {
		registration.addIngredientInfo(
				IngredientJS.of(o).getStacks().stream().map(ItemStackJS::getItemStack).toList(),
				VanillaTypes.ITEM,
				toComponent(s)
		);
	}

	public <T> void addForType(IIngredientType<T> type, Object o, Text[] s) {
		var targets = new HashSet<>(ListJS.orSelf(o).map(String::valueOf));
		var manager = registration.getIngredientManager();
		var helper = manager.getIngredientHelper(type);
		registration.addIngredientInfo(
				manager.getAllIngredients(type)
						.stream()
						.filter(t -> targets.contains(helper.getWildcardId(t)))
						.toList(),
				type, toComponent(s));
	}

	public static Component[] toComponent(Text[] s) {
		return Util.make(new Component[s.length], arr -> {
			for (var i = 0; i < s.length; i++) {
				arr[i] = s[i].component();
			}
		});
	}

}