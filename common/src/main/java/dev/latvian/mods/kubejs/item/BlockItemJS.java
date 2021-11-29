package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.kubejs.block.BlockItemBuilder;
import net.minecraft.world.item.BlockItem;

/**
 * @author LatvianModder
 */
public class BlockItemJS extends BlockItem {
	public BlockItemJS(BlockItemBuilder p) {
		super(p.blockBuilder.block, p.createItemProperties());
	}
}