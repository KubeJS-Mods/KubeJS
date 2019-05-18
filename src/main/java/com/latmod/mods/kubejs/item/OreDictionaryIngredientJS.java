package com.latmod.mods.kubejs.item;

import net.minecraftforge.oredict.OreDictionary;

/**
 * @author LatvianModder
 */
public class OreDictionaryIngredientJS implements IIngredientJS
{
	public final String oreName;
	public final int oreID;

	public OreDictionaryIngredientJS(String ore)
	{
		oreName = ore;
		oreID = OreDictionary.getOreID(oreName);
	}

	@Override
	public boolean test(ItemStackJS stack)
	{
		for (int id : OreDictionary.getOreIDs(stack.itemStack()))
		{
			if (oreID == id)
			{
				return true;
			}
		}

		return false;
	}
}