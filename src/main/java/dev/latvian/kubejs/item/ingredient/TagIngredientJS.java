package dev.latvian.kubejs.item.ingredient;

import dev.latvian.kubejs.item.BoundItemStackJS;
import dev.latvian.kubejs.item.EmptyItemStackJS;
import dev.latvian.kubejs.item.ItemStackJS;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author LatvianModder
 */
public class TagIngredientJS implements IngredientJS
{
	private final ResourceLocation tag;

	public TagIngredientJS(ResourceLocation t)
	{
		tag = t;
	}

	public ResourceLocation getTag()
	{
		return tag;
	}

	@Override
	public boolean test(ItemStackJS stack)
	{
		return !stack.isEmpty() && stack.getItem().getTags().contains(tag);
	}

	@Override
	public boolean testVanilla(ItemStack stack)
	{
		return !stack.isEmpty() && stack.getItem().getTags().contains(tag);
	}

	@Override
	public Set<ItemStackJS> getStacks()
	{
		Tag<Item> t = ItemTags.getCollection().get(tag);

		if (t != null && t.getAllElements().size() > 0)
		{
			NonNullList<ItemStack> list = NonNullList.create();

			for (Item item : t.getAllElements())
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
		Tag<Item> t = ItemTags.getCollection().get(tag);

		if (t != null && t.getAllElements().size() > 0)
		{
			NonNullList<ItemStack> list = NonNullList.create();

			for (Item item : t.getAllElements())
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
		Tag<Item> t = ItemTags.getCollection().get(tag);
		return t != null && t.getAllElements().isEmpty();
	}

	@Override
	public String toString()
	{
		return "tag:" + tag;
	}
}