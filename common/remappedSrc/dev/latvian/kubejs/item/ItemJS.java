package dev.latvian.kubejs.item;

import dev.latvian.kubejs.text.Text;
import org.jetbrains.annotations.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import java.util.List;

/**
 * @author LatvianModder
 */
public class ItemJS extends Item
{
	public final ItemBuilder properties;
	private ItemStack containerItem;

	public ItemJS(ItemBuilder p)
	{
		super(p.createItemProperties());
		properties = p;
	}

	@Override
	public boolean isFoil(ItemStack stack)
	{
		return properties.glow || super.isFoil(stack);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn)
	{
		super.appendHoverText(stack, worldIn, tooltip, flagIn);

		for (Text text : properties.tooltip)
		{
			tooltip.add(text.component());
		}
	}
}