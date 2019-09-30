package dev.latvian.kubejs.entity;

import dev.latvian.kubejs.documentation.P;
import dev.latvian.kubejs.documentation.T;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.world.WorldJS;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class ItemFrameEntityJS extends EntityJS
{
	private final EntityItemFrame itemFrameEntity;

	public ItemFrameEntityJS(WorldJS w, EntityItemFrame e)
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

	public void setItem(@P("item") @T(ItemStackJS.class) Object item)
	{
		itemFrameEntity.setDisplayedItem(ItemStackJS.of(item).getItemStack());
	}

	public int getFrameRotation()
	{
		return itemFrameEntity.getRotation();
	}

	public void setFrameRotation(@P("rotation") int rotation)
	{
		itemFrameEntity.setItemRotation(rotation);
	}
}
