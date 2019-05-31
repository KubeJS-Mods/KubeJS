package com.latmod.mods.kubejs.item;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.OreIngredient;

import java.util.HashSet;
import java.util.Set;

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

	@Override
	public Set<ItemStackJS> getStacks()
	{
		Set<ItemStackJS> set = new HashSet<>();

		for (ItemStack stack : OreDictionary.getOres(oreName))
		{
			set.add(new ItemStackJS.Bound(stack));
		}

		return set;
	}

	@Override
	public Ingredient createVanillaIngredient()
	{
		return new OreIngredient(oreName);
	}
}