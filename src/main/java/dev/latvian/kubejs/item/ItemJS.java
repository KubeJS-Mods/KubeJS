package dev.latvian.kubejs.item;

import dev.latvian.kubejs.text.Text;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class ItemJS extends Item
{
	public static final Map<ResourceLocation, ItemJS> KUBEJS_ITEMS = new HashMap<>();

	public final ItemBuilder properties;
	private ItemStack containerItem;

	public ItemJS(ItemBuilder p)
	{
		super(p.createItemProperties());
		properties = p;
	}

	@Override
	public boolean hasEffect(ItemStack stack)
	{
		return properties.glow || super.hasEffect(stack);
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
	{
		super.addInformation(stack, worldIn, tooltip, flagIn);

		for (Text text : properties.tooltip)
		{
			tooltip.add(text.component());
		}
	}
}