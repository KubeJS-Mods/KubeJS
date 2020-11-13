package dev.latvian.kubejs.client;

import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.text.Text;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

/**
 * @author LatvianModder
 */
public class ClientItemTooltipEventJS extends EventJS
{
	private final ItemStack stack;
	private final List<Component> lines;
	private final TooltipFlag flag;

	public ClientItemTooltipEventJS(ItemStack stack, List<Component> lines, TooltipFlag flag)
	{
		this.stack = stack;
		this.lines = lines;
		this.flag = flag;
	}

	public ItemStackJS getItem()
	{
		return ItemStackJS.of(stack);
	}

	public boolean isAdvanced()
	{
		return flag.isAdvanced();
	}

	public void add(Object text)
	{
		lines.add(Text.of(text).component());
	}
}