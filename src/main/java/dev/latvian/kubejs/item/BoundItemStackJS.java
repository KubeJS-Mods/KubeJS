package dev.latvian.kubejs.item;

import dev.latvian.kubejs.text.Text;
import dev.latvian.kubejs.util.nbt.NBTBaseJS;
import dev.latvian.kubejs.util.nbt.NBTCompoundJS;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

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
	public void setDamage(int damage)
	{
		stack.setDamage(damage);
	}

	@Override
	public int getDamage()
	{
		return stack.getDamage();
	}

	@Override
	public void setNbt(@Nullable Object nbt)
	{
		stack.setTag(NBTBaseJS.of(nbt).asCompound().createNBT());
	}

	@Override
	public NBTCompoundJS getNbt()
	{
		return NBTBaseJS.of(stack.getTag()).asCompound();
	}

	@Override
	public void setName(Object displayName)
	{
		stack.setDisplayName(Text.of(displayName).component());
	}

	@Override
	public boolean test(ItemStackJS stack)
	{
		return stack instanceof BoundItemStackJS ? testVanilla(((BoundItemStackJS) stack).stack) : super.test(stack);
	}

	@Override
	public boolean testVanilla(ItemStack stack2)
	{
		if (stack2.getCount() >= stack.getCount() && stack.getItem() == stack2.getItem())
		{
			CompoundNBT nbt = stack.getTag();
			CompoundNBT nbt2 = stack2.getTag();
			return Objects.equals(nbt, nbt2);
		}

		return false;
	}
}