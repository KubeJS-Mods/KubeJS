package dev.latvian.kubejs.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class ItemJS extends Item
{
	public final ItemBuilder properties = ItemBuilder.current;
	private Item containerItem;

	public ItemJS()
	{
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

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
	{
		super.addInformation(stack, worldIn, tooltip, flagIn);

		for (ITextComponent component : properties.tooltip)
		{
			tooltip.add(component.getFormattedText());
		}
	}
}