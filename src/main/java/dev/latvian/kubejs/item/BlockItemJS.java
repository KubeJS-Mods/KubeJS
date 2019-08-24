package dev.latvian.kubejs.item;

import net.minecraft.block.Block;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

/**
 * @author LatvianModder
 */
public class BlockItemJS extends ItemBlock
{
	public final ItemProperties properties;

	public BlockItemJS(Block b, ItemProperties p)
	{
		super(b);
		properties = p;

		if (!properties.translationKey.isEmpty())
		{
			setTranslationKey(properties.translationKey);
		}

		setMaxStackSize(properties.maxStackSize);
	}

	@Override
	public EnumRarity getRarity(ItemStack stack)
	{
		return properties.rarity;
	}

	@Override
	public boolean hasEffect(ItemStack stack)
	{
		return properties.glow || super.hasEffect(stack);
	}
}