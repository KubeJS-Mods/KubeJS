package dev.latvian.kubejs.entity;

import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.world.WorldJS;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class ItemFrameEntityJS extends EntityJS
{
	private final ItemFrameEntity itemFrameEntity;

	public ItemFrameEntityJS(WorldJS w, ItemFrameEntity e)
	{
		super(w, e);
		itemFrameEntity = e;
	}

	@Override
	public boolean isFrame()
	{
		return true;
	}

	@Override
	@Nullable
	public ItemStackJS getItem()
	{
		ItemStack stack = itemFrameEntity.getDisplayedItem();
		return stack.isEmpty() ? null : ItemStackJS.of(stack);
	}

	public void setItem(Object item)
	{
		itemFrameEntity.setDisplayedItem(ItemStackJS.of(item).getItemStack());
	}

	public int getFrameRotation()
	{
		return itemFrameEntity.getRotation();
	}

	public void setFrameRotation(int rotation)
	{
		itemFrameEntity.setItemRotation(rotation);
	}
}
