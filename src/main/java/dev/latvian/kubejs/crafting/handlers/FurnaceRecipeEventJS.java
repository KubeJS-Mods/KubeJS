package dev.latvian.kubejs.crafting.handlers;

import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.item.BoundItemStackJS;
import dev.latvian.kubejs.item.IIngredientJS;
import dev.latvian.kubejs.item.ItemStackJS;
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
		FurnaceRecipes.instance().getSmeltingList().values().removeIf(stack -> output.test(new BoundItemStackJS(stack)));
	}

	public void removeInput(IIngredientJS input)
	{
		FurnaceRecipes.instance().getSmeltingList().keySet().removeIf(stack -> input.test(new BoundItemStackJS(stack)));
	}
}