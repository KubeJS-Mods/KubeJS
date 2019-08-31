package dev.latvian.kubejs.item;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;
import java.util.Collections;
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
	@Nullable
	public NBTTagCompound rawNBT()
	{
		return null;
	}

	@Override
	public ItemStackJS caps(@Nullable Object o)
	{
		return this;
	}

	@Override
	@Nullable
	public NBTTagCompound rawCaps()
	{
		return null;
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
	public Set<ItemStackJS> stacks()
	{
		return Collections.emptySet();
	}

	@Override
	public Ingredient createVanillaIngredient()
	{
		return Ingredient.EMPTY;
	}
}