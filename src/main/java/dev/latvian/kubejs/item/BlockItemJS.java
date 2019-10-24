package dev.latvian.kubejs.item;

import dev.latvian.kubejs.block.BlockJS;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

/**
 * @author LatvianModder
 */
public class BlockItemJS extends ItemBlock
{
	public final ItemBuilder properties;

	public BlockItemJS(BlockJS b, ItemBuilder p)
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