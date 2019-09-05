package dev.latvian.kubejs.item;

import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class ItemJS extends Item
{
	public final ItemBuilder properties;
	private Item containerItem;

	public ItemJS(ItemBuilder p)
	{
		properties = p;
		setTranslationKey(properties.translationKey);
		setMaxStackSize(properties.maxStackSize);

		for (Map.Entry<String, Integer> entry : properties.tools.entrySet())
		{
			setHarvestLevel(entry.getKey(), entry.getValue());
		}
	}

	@Nullable
	@Override
	public Item getContainerItem()
	{
		if (containerItem == null)
		{
			containerItem = properties.containerItem == null ? null : REGISTRY.getObject(properties.containerItem.mc());

			if (containerItem == null)
			{
				containerItem = Items.AIR;
			}
		}

		return containerItem == Items.AIR ? null : containerItem;
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