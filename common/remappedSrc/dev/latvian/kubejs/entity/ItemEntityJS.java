package dev.latvian.kubejs.entity;

import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.world.WorldJS;
import org.jetbrains.annotations.Nullable;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import java.util.UUID;

/**
 * @author LatvianModder
 */
public class ItemEntityJS extends EntityJS
{
	private final ItemEntity itemEntity;

	public ItemEntityJS(WorldJS w, ItemEntity e)
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

	public void setItem(Object item)
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

	@Nullable
	public UUID getOwner()
	{
		return itemEntity.getOwner();
	}

	public void setOwner(UUID owner)
	{
		itemEntity.setOwner(owner);
	}

	@Nullable
	public UUID getThrower()
	{
		return itemEntity.getThrower();
	}

	public void setThrower(UUID thrower)
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
		itemEntity.setPickUpDelay(ticks);
	}

	public void setNoDespawn()
	{
		itemEntity.setExtendedLifetime();
	}
}