package dev.latvian.kubejs.item;

import dev.latvian.kubejs.block.BlockJS;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;

/**
 * @author LatvianModder
 */
public class BlockItemJS extends BlockItem
{
	public final ItemBuilder properties;

	public BlockItemJS(BlockJS b, ItemBuilder p)
	{
		super(b, p.createItemProperties());
		properties = p;
	}

	@Override
	public boolean hasEffect(ItemStack stack)
	{
		return properties.glow || super.hasEffect(stack);
	}
}