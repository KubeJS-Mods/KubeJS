package dev.latvian.kubejs.item;

import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.item.ingredient.MatchAllIngredientJS;
import dev.latvian.kubejs.util.ID;
import dev.latvian.kubejs.util.nbt.NBTCompoundJS;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author LatvianModder
 */
public class EmptyItemStackJS extends ItemStackJS
{
	public static final EmptyItemStackJS INSTANCE = new EmptyItemStackJS();

	private EmptyItemStackJS()
	{
	}

	@Override
	public Item item()
	{
		return Items.AIR;
	}

	@Override
	public ItemStackJS copy()
	{
		return this;
	}

	@Override
	public ItemStackJS count(int c)
	{
		return this;
	}

	@Override
	public int count()
	{
		return 0;
	}

	@Override
	public boolean isEmpty()
	{
		return true;
	}

	@Override
	public ItemStackJS data(int d)
	{
		return this;
	}

	@Override
	public int data()
	{
		return 0;
	}

	@Override
	public ItemStackJS nbt(@Nullable Object o)
	{
		return this;
	}

	@Override
	public NBTCompoundJS nbt()
	{
		return NBTCompoundJS.NULL;
	}

	@Override
	public ItemStackJS caps(@Nullable Object o)
	{
		return this;
	}

	@Override
	public NBTCompoundJS caps()
	{
		return NBTCompoundJS.NULL;
	}

	@Override
	public ItemStack itemStack()
	{
		return ItemStack.EMPTY;
	}

	public String toString()
	{
		return "air";
	}

	@Override
	public boolean test(ItemStackJS stack)
	{
		return false;
	}

	@Override
	public boolean test(ItemStack stack)
	{
		return false;
	}

	@Override
	public Set<ItemStackJS> getStacks()
	{
		return Collections.emptySet();
	}

	@Override
	public IngredientJS not()
	{
		return MatchAllIngredientJS.INSTANCE;
	}

	@Override
	public Map<ID, Integer> getEnchantments()
	{
		return new LinkedHashMap<>();
	}

	@Override
	public void setEnchantments(Map<ID, Integer> map)
	{
	}

	@Override
	public ItemStackJS enchant(Map<Object, Integer> ma)
	{
		return this;
	}

	@Override
	public int getEnchantment(Object id)
	{
		return 0;
	}

	@Override
	public String getMod()
	{
		return "minecraft";
	}
}