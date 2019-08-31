package dev.latvian.kubejs.item;

import dev.latvian.kubejs.util.ID;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

/**
 * @author LatvianModder
 */
public abstract class ItemStackJS implements IIngredientJS
{
	public abstract Item item();

	public ID id()
	{
		return new ID(item().getRegistryName());
	}

	public abstract ItemStackJS copy();

	public abstract ItemStackJS count(int c);

	public abstract int count();

	public boolean isEmpty()
	{
		return count() <= 0;
	}

	public abstract ItemStackJS data(int d);

	public abstract int data();

	public abstract ItemStackJS nbt(@Nullable Object o);

	@Nullable
	public abstract NBTTagCompound rawNBT();

	public abstract ItemStackJS caps(@Nullable Object o);

	@Nullable
	public abstract NBTTagCompound rawCaps();

	public abstract ItemStack itemStack();

	public ItemStackJS grow(int c)
	{
		return count(count() + c);
	}

	public ItemStackJS shrink(int c)
	{
		return grow(-c);
	}

	public String nbt()
	{
		NBTTagCompound nbt = rawNBT();
		return nbt == null ? "null" : nbt.toString();
	}

	public String caps()
	{
		NBTTagCompound nbt = rawCaps();
		return nbt == null ? "null" : nbt.toString();
	}

	public ItemStackJS name(String displayName)
	{
		NBTTagCompound nbt = rawNBT();

		if (nbt == null)
		{
			nbt = new NBTTagCompound();
		}

		NBTTagCompound n = nbt.getCompoundTag("display");
		n.setString("Name", displayName);
		nbt.setTag("display", n);
		return nbt(nbt);
	}

	public String name()
	{
		return itemStack().getDisplayName();
	}

	public String toString()
	{
		StringBuilder builder = new StringBuilder();

		if (count() > 1)
		{
			builder.append(count());
			builder.append('x');
			builder.append(' ');
		}

		builder.append(item().getRegistryName());

		if (item().getHasSubtypes())
		{
			builder.append('@');
			builder.append(data());
		}

		NBTTagCompound nbt = rawNBT();
		NBTTagCompound caps = rawCaps();

		if (nbt != null || caps != null)
		{
			builder.append(' ');
			builder.append(nbt());
		}

		if (caps != null)
		{
			builder.append(' ');
			builder.append(caps());
		}

		return builder.toString();
	}

	@Override
	public boolean test(ItemStackJS stack)
	{
		return item() == stack.item() && (data() == 32767 || data() == stack.data()) && Objects.equals(rawNBT(), stack.rawNBT());
	}

	@Override
	public Set<ItemStackJS> stacks()
	{
		return Collections.singleton(this);
	}

	@Override
	public Ingredient createVanillaIngredient()
	{
		return Ingredient.fromStacks(itemStack());
	}

	@Override
	public int hashCode()
	{
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof ItemStackJS)
		{
			ItemStackJS stack = (ItemStackJS) obj;
			return item() == stack.item() && data() == stack.data() && Objects.equals(rawNBT(), stack.rawNBT());
		}

		return false;
	}
}