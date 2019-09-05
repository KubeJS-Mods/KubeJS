package dev.latvian.kubejs.crafting.handlers;

import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
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

	public void remove(IngredientJS output)
	{
		FurnaceRecipes.instance().getSmeltingList().values().removeIf(output);
	}

	public void removeInput(IngredientJS input)
	{
		FurnaceRecipes.instance().getSmeltingList().keySet().removeIf(input);
	}
}