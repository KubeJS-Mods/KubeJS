package dev.latvian.kubejs.item.ingredient;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.item.BoundItemStackJS;
import dev.latvian.kubejs.item.EmptyItemStackJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.util.UtilsJS;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author LatvianModder
 */
public class TagIngredientJS implements IngredientJS
{
	private static final Map<String, ItemTags.Wrapper> TAG_CACHE = new HashMap<>();

	private final String tag;
	private final ItemTags.Wrapper cachedTag;

	public TagIngredientJS(String t)
	{
		tag = UtilsJS.getID(t);
		cachedTag = TAG_CACHE.computeIfAbsent(tag, i -> new ItemTags.Wrapper(new ResourceLocation(i)));
	}

	public String getTag()
	{
		return tag;
	}

	@Override
	public boolean test(ItemStackJS stack)
	{
		return !stack.isEmpty() && cachedTag.contains(stack.getItem());
	}

	@Override
	public boolean testVanilla(ItemStack stack)
	{
		return !stack.isEmpty() && cachedTag.contains(stack.getItem());
	}

	@Override
	public Set<ItemStackJS> getStacks()
	{
		if (cachedTag.getAllElements().size() > 0)
		{
			NonNullList<ItemStack> list = NonNullList.create();

			for (Item item : cachedTag.getAllElements())
			{
				item.fillItemGroup(ItemGroup.SEARCH, list);
			}

			Set<ItemStackJS> set = new LinkedHashSet<>();

			for (ItemStack stack1 : list)
			{
				if (!stack1.isEmpty())
				{
					set.add(new BoundItemStackJS(stack1));
				}
			}

			return set;
		}

		return Collections.emptySet();
	}

	@Override
	public ItemStackJS getFirst()
	{
		if (cachedTag.getAllElements().size() > 0)
		{
			NonNullList<ItemStack> list = NonNullList.create();

			for (Item item : cachedTag.getAllElements())
			{
				item.fillItemGroup(ItemGroup.SEARCH, list);

				for (ItemStack stack : list)
				{
					if (!stack.isEmpty())
					{
						return new BoundItemStackJS(stack);
					}
				}

				list.clear();
			}
		}

		return EmptyItemStackJS.INSTANCE;
	}

	@Override
	public boolean isEmpty()
	{
		return cachedTag.getAllElements().isEmpty();
	}

	@Override
	public String toString()
	{
		return "'#" + tag + "'";
	}

	@Override
	public JsonElement toJson()
	{
		JsonObject json = new JsonObject();
		json.addProperty("tag", tag);
		return json;
	}

	@Override
	public boolean anyStackMatches(IngredientJS ingredient)
	{
		if (ingredient instanceof TagIngredientJS && tag.equals(((TagIngredientJS) ingredient).tag))
		{
			return true;
		}

		return IngredientJS.super.anyStackMatches(ingredient);
	}
}