package dev.latvian.kubejs.item;

import dev.latvian.kubejs.text.Text;
import dev.latvian.kubejs.text.TextTranslate;
import dev.latvian.kubejs.util.nbt.NBTBaseJS;
import dev.latvian.kubejs.util.nbt.NBTCompoundJS;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * @author LatvianModder
 */
public class BoundItemStackJS extends ItemStackJS
{
	private final ItemStack stack;

	public BoundItemStackJS(ItemStack is)
	{
		stack = is;
	}

	@Override
	public Item getItem()
	{
		return stack.getItem();
	}

	@Override
	public ItemStack getItemStack()
	{
		return stack;
	}

	@Override
	public ItemStackJS getCopy()
	{
		return new BoundItemStackJS(stack.copy());
	}

	@Override
	public void setCount(int c)
	{
		stack.setCount(c);
	}

	@Override
	public int getCount()
	{
		return stack.getCount();
	}

	@Override
	public boolean isEmpty()
	{
		return stack.isEmpty();
	}

	@Override
	public void setData(int data)
	{
		stack.setItemDamage(data);
	}

	@Override
	public int getData()
	{
		return stack.getMetadata();
	}

	@Override
	public void setNbt(@Nullable Object nbt)
	{
		stack.setTagCompound(NBTBaseJS.of(nbt).asCompound().createNBT());
	}

	@Override
	public NBTCompoundJS getNbt()
	{
		return NBTBaseJS.of(stack.getTagCompound()).asCompound();
	}

	@Override
	public void setName(Object displayName)
	{
		Text t = Text.of(displayName);
		NBTCompoundJS nbt = getNbtOrNew();

		if (t instanceof TextTranslate)
		{
			stack.setTranslatableName(((TextTranslate) t).getKey());
		}
		else
		{
			stack.setStackDisplayName(t.getFormattedString());
		}

		setNbt(nbt);
	}

	@Override
	public boolean test(ItemStackJS stack)
	{
		return stack instanceof BoundItemStackJS ? test(((BoundItemStackJS) stack).stack) : super.test(stack);
	}

	@Override
	public boolean test(ItemStack stack2)
	{
		if (stack2.getCount() >= stack.getCount() && stack.getItem() == stack2.getItem())
		{
			int d = stack.getMetadata();

			if (d == OreDictionary.WILDCARD_VALUE || d == stack2.getMetadata())
			{
				NBTTagCompound nbt = stack.getTagCompound();
				NBTTagCompound nbt2 = stack2.getTagCompound();
				return Objects.equals(nbt, nbt2);
			}
		}

		return false;
	}
}