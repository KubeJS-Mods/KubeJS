package com.latmod.mods.kubejs.crafting.handlers;

import com.latmod.mods.kubejs.events.EventJS;
import com.latmod.mods.kubejs.item.IIngredientJS;
import com.latmod.mods.kubejs.item.ItemStackJS;
import net.minecraft.item.crafting.FurnaceRecipes;

/**
 * @author LatvianModder
 */
public class FurnaceRecipeEventJS extends EventJS
{
	public void add(ItemStackJS input, ItemStackJS output, float experience)
	{
		FurnaceRecipes.instance().addSmeltingRecipe(input.itemStack(), output.itemStack(), experience);
	}

	public void add(ItemStackJS input, ItemStackJS output)
	{
		add(input, output, 0F);
	}

	public void remove(IIngredientJS output)
	{
		FurnaceRecipes.instance().getSmeltingList().values().removeIf(stack -> output.test(new ItemStackJS.Bound(stack)));
	}

	public void removeInput(IIngredientJS input)
	{
		FurnaceRecipes.instance().getSmeltingList().keySet().removeIf(stack -> input.test(new ItemStackJS.Bound(stack)));
	}
}