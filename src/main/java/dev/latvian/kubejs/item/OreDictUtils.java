package dev.latvian.kubejs.item;

import dev.latvian.kubejs.item.ingredient.IngredientJS;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author LatvianModder
 */
public class OreDictUtils
{
	public static final List<String> DYES = Collections.unmodifiableList(Arrays.asList("Black", "Red", "Green", "Brown", "Blue", "Purple", "Cyan", "LightGray", "Gray", "Pink", "Lime", "Yellow", "LightBlue", "Magenta", "Orange", "White"));

	public static void registerOre(String name, IngredientJS ingredient)
	{
		for (ItemStackJS stack : ingredient.stacks())
		{
			registerOre(name, ingredient);
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