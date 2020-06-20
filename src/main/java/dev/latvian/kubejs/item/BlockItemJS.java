package dev.latvian.kubejs.item;

import dev.latvian.kubejs.block.BlockItemBuilder;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;

/**
 * @author LatvianModder
 */
public class BlockItemJS extends BlockItem
{
	public final BlockItemBuilder properties;

	public BlockItemJS(BlockItemBuilder p)
	{
		super(p.blockBuilder.block, p.createItemProperties());
		properties = p;
	}

	@Override
	public boolean hasEffect(ItemStack stack)
	{
		return properties.glow || super.hasEffect(stack);
	}
}