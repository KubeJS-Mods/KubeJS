package dev.latvian.mods.kubejs.integration.forge.jei;

import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.fluid.FluidStackJS;
import dev.latvian.mods.kubejs.util.ListJS;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.stream.Collectors;

/**
 * @author LatvianModder
 */

public class InformationJEIEventJS extends EventJS {
	private final IRecipeRegistration registration;

	public InformationJEIEventJS(IRecipeRegistration reg) {
		registration = reg;
	}

	public void addItem(Ingredient item, Component[] s) {
		registration.addIngredientInfo(item.kjs$getStacks().toList(), VanillaTypes.ITEM_STACK, s);
	}

	public void addFluid(Object fluid, Component[] s) {
		registration.addIngredientInfo(JEIPlugin.fromArchitectury(FluidStackJS.of(fluid).getFluidStack()), ForgeTypes.FLUID_STACK, s);
	}

	public <T> void addForType(IIngredientType<T> type, Object o, Component[] s) {
		var targets = ListJS.orSelf(o).stream().map(String::valueOf).collect(Collectors.toSet());
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