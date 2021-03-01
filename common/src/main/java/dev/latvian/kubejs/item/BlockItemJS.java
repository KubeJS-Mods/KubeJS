package dev.latvian.kubejs.item;

import dev.latvian.kubejs.block.BlockItemBuilder;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;

/**
 * @author LatvianModder
 */
public class BlockItemJS extends BlockItem {
	public final BlockItemBuilder properties;

	public BlockItemJS(BlockItemBuilder p) {
		super(p.blockBuilder.block, p.createItemProperties());
		properties = p;
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return properties.glow || super.isFoil(stack);
	}
}