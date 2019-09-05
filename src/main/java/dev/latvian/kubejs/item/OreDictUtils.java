package dev.latvian.kubejs.item;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.util.UtilsJS;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
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

	private static List<NonNullList<ItemStack>> idToStack = null;

	public static void add(IngredientJS ingredient, String name)
	{
		for (ItemStackJS stack : ingredient.getStacks())
		{
			OreDictionary.registerOre(name, stack.itemStack());
		}
	}

	public static void remove(IngredientJS ingredient, String name)
	{
		if (idToStack == null)
		{
			idToStack = UtilsJS.getField(OreDictionary.class, "idToStack").staticGet();

			if (idToStack == null)
			{
				KubeJS.LOGGER.error("Failed to load OreDictionary map, can't remove names!");
				idToStack = Collections.emptyList();
			}
		}

		int id = OreDictionary.getOreID(name);

		if (id >= 0 && id < idToStack.size())
		{
			idToStack.get(id).removeIf(ingredient);
		}
	}

	public static List<String> getNames(ItemStackJS item)
	{
		if (item.isEmpty())
		{
			return Collections.emptyList();
		}

		int[] ai = OreDictionary.getOreIDs(item.itemStack());
		List<String> list = new ObjectArrayList<>(ai.length);

		for (int value : ai)
		{
			list.add(OreDictionary.getOreName(value));
		}

		return list;
	}
}