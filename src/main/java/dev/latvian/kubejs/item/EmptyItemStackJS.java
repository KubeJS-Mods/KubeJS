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
	public Item getItem()
	{
		return Items.AIR;
	}

	@Override
	public ItemStack getItemStack()
	{
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStackJS getCopy()
	{
		return this;
	}

	@Override
	public void setCount(int c)
	{
	}

	@Override
	public int getCount()
	{
		return 0;
	}

	@Override
	public boolean isEmpty()
	{
		return true;
	}

	@Override
	public void setData(int data)
	{
	}

	@Override
	public int getData()
	{
		return 0;
	}

	@Override
	public void setNbt(@Nullable Object nbt)
	{
	}

	@Override
	public NBTCompoundJS getNbt()
	{
		return NBTCompoundJS.NULL;
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
	public ItemStackJS getFirst()
	{
		return this;
	}

	@Override
	public IngredientJS not()
	{
		return MatchAllIngredientJS.INSTANCE;
	}

	@Override
	public void setName(String displayName)
	{
	}

	@Override
	public void setTranslatableName(String translatableName)
	{
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

	@Override
	public boolean equals(Object o)
	{
		return ItemStackJS.of(o).isEmpty();
	}

	@Override
	public boolean strongEquals(Object o)
	{
		return ItemStackJS.of(o).isEmpty();
	}
}