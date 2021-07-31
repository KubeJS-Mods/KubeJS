package dev.latvian.kubejs.item;

import dev.latvian.kubejs.block.BlockItemBuilder;
import net.minecraft.world.item.BlockItem;

/**
 * @author LatvianModder
 */
public class BlockItemJS extends BlockItem {
	public BlockItemJS(BlockItemBuilder p) {
		super(p.blockBuilder.block, p.createItemProperties());
	}
}