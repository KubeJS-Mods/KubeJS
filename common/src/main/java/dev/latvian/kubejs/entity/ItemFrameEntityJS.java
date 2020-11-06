package dev.latvian.kubejs.entity;

import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.world.WorldJS;
import org.jetbrains.annotations.Nullable;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemStack;

/**
 * @author LatvianModder
 */
public class ItemFrameEntityJS extends EntityJS
{
	private final ItemFrame itemFrameEntity;

	public ItemFrameEntityJS(WorldJS w, ItemFrame e)
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
		ItemStack stack = itemFrameEntity.getItem();
		return stack.isEmpty() ? null : ItemStackJS.of(stack);
	}

	public void setItem(Object item)
	{
		itemFrameEntity.setItem(ItemStackJS.of(item).getItemStack());
	}

	public int getFrameRotation()
	{
		return itemFrameEntity.getRotation();
	}

	public void setFrameRotation(int rotation)
	{
		itemFrameEntity.setRotation(rotation);
	}
}
