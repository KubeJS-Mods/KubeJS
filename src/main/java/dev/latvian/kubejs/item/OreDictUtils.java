package dev.latvian.kubejs.item;

import dev.latvian.kubejs.util.UtilsJS;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author LatvianModder
 */
public enum OreDictUtils
{
	INSTANCE;

	public final List<String> dyes = Collections.unmodifiableList(Arrays.asList("Black", "Red", "Green", "Brown", "Blue", "Purple", "Cyan", "LightGray", "Gray", "Pink", "Lime", "Yellow", "LightBlue", "Magenta", "Orange", "White"));

	public void registerOre(String name, Object... items)
	{
		for (Object item : items)
		{
			ItemStackJS js = UtilsJS.INSTANCE.item(item);

			if (!js.isEmpty())
			{
				OreDictionary.registerOre(name, js.itemStack());
			}
		}
	}

	public List<String> getNames(Object item)
	{
		ItemStackJS itemStackJS = UtilsJS.INSTANCE.item(item);

		if (itemStackJS.isEmpty())
		{
			return Collections.emptyList();
		}

		int[] ai = OreDictionary.getOreIDs(itemStackJS.itemStack());
		List<String> list = new ObjectArrayList<>(ai.length);

		for (int i = 0; i < ai.length; i++)
		{
			list.add(OreDictionary.getOreName(ai[i]));
		}

		return list;
	}

	public List<ItemStackJS> getItems(String ore)
	{
		List<ItemStack> l = OreDictionary.getOres(ore);

		if (l.isEmpty())
		{
			return Collections.emptyList();
		}

		List<ItemStackJS> list = new ObjectArrayList<>(l.size());

		for (ItemStack stack : l)
		{
			list.add(UtilsJS.INSTANCE.item(stack));
		}

		return list;
	}
}