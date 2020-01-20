package dev.latvian.kubejs.item;

import dev.latvian.kubejs.block.BlockJS;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class BlockItemJS extends BlockItem
{
	public static final Map<ResourceLocation, BlockItemJS> KUBEJS_BLOCK_ITEMS = new HashMap<>();

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