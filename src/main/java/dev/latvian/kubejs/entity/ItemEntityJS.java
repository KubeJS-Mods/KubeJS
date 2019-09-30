package dev.latvian.kubejs.entity;

import dev.latvian.kubejs.documentation.P;
import dev.latvian.kubejs.documentation.T;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.world.WorldJS;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class ItemEntityJS extends EntityJS
{
	private final EntityItem itemEntity;

	public ItemEntityJS(WorldJS w, EntityItem e)
	{
		super(w, e);
		itemEntity = e;
	}

	@Override
	@Nullable
	public ItemStackJS getItem()
	{
		ItemStack stack = itemEntity.getItem();
		return stack.isEmpty() ? null : ItemStackJS.of(stack);
	}

	public void setItem(@P("item") @T(ItemStackJS.class) Object item)
	{
		itemEntity.setItem(ItemStackJS.of(item).getItemStack());
	}

	public int getLifespan()
	{
		return itemEntity.lifespan;
	}

	public void setLifespan(int lifespan)
	{
		itemEntity.lifespan = lifespan;
	}

	public String getOwner()
	{
		return itemEntity.getOwner();
	}

	public void setOwner(String owner)
	{
		itemEntity.setOwner(owner);
	}

	public String getThrower()
	{
		return itemEntity.getThrower();
	}

	public void setThrower(String thrower)
	{
		itemEntity.setThrower(thrower);
	}

	public void setDefaultPickupDelay()
	{
		setPickupDelay(10);
	}

	public void setNoPickupDelay()
	{
		setPickupDelay(0);
	}

	public void setInfinitePickupDelay()
	{
		setPickupDelay(Short.MAX_VALUE);
	}

	public void setPickupDelay(int ticks)
	{
		itemEntity.setPickupDelay(ticks);
	}

	public void setNoDespawn()
	{
		itemEntity.setNoDespawn();
	}
}