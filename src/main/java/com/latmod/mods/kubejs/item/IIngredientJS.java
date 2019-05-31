package com.latmod.mods.kubejs.item;

import net.minecraft.item.crafting.Ingredient;

import java.util.Collections;
import java.util.Set;
import java.util.function.Predicate;

/**
 * @author LatvianModder
 */
@FunctionalInterface
public interface IIngredientJS extends Predicate<ItemStackJS>
{
	default Set<ItemStackJS> getStacks()
	{
		return Collections.emptySet();
	}

	default Ingredient createVanillaIngredient()
	{
		return Ingredient.EMPTY;
	}
}