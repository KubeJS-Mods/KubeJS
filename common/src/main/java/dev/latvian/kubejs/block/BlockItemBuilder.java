package dev.latvian.kubejs.block;

import dev.latvian.kubejs.item.BlockItemJS;
import dev.latvian.kubejs.item.ItemBuilder;

/**
 * @author LatvianModder
 */
public class BlockItemBuilder extends ItemBuilder
{
	public BlockBuilder blockBuilder;
	public BlockItemJS blockItem;

	public BlockItemBuilder(String i)
	{
		super(i);
	}

	@Override
	public String getType()
	{
		return "block";
	}
}
