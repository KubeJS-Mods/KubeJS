package dev.latvian.kubejs.item;

import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.util.UtilsJS;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @author LatvianModder
 */
public class OreDictUtils
{
	public static final List<String> DYES = Collections.unmodifiableList(Arrays.asList("Black", "Red", "Green", "Brown", "Blue", "Purple", "Cyan", "LightGray", "Gray", "Pink", "Lime", "Yellow", "LightBlue", "Magenta", "Orange", "White"));

	private static List<NonNullList<ItemStack>> idToStack = null;

	public static void add(IngredientJS ingredient, String name)
	{
		for (ItemStackJS stack : ingredient.stacks())
		{
			OreDictionary.registerOre(name, stack.itemStack());
		}
	}

	public static void remove(IngredientJS ingredient, String name)
	{
		if (idToStack == null)
		{
			idToStack = UtilsJS.field(OreDictionary.class, "idToStack").staticGet();

			if (idToStack == null)
			{
				idToStack = Collections.emptyList();
			}
		}

		int id = OreDictionary.getOreID(name);

		if (id >= 0 && id < idToStack.size())
		{
			Iterator<ItemStack> itr = idToStack.get(id).iterator();
			Ingredient ingredient1 = ingredient.createVanillaIngredient();

			while (itr.hasNext())
			{
				ItemStack stack = itr.next();

				if (ingredient1.apply(stack))
				{
					itr.remove();
				}
			}
		}
	}

	public static List<String> names(ItemStackJS item)
	{
		if (item.isEmpty())
		{
			return Collections.emptyList();
		}

		int[] ai = OreDictionary.getOreIDs(item.itemStack());
		List<String> list = new ObjectArrayList<>(ai.length);

		for (int i = 0; i < ai.length; i++)
		{
			list.add(OreDictionary.getOreName(ai[i]));
		}

		return list;
	}

	public static List<ItemStackJS> items(String ore)
	{
		List<ItemStack> l = OreDictionary.getOres(ore);

		if (l.isEmpty())
		{
			return Collections.emptyList();
		}

		List<ItemStackJS> list = new ObjectArrayList<>(l.size());

		for (ItemStack stack : l)
		{
			list.add(ItemStackJS.of(stack));
		}

		return list;
	}
}