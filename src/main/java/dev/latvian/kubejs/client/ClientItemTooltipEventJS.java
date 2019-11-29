package dev.latvian.kubejs.client;

import dev.latvian.kubejs.documentation.P;
import dev.latvian.kubejs.documentation.T;
import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.text.Text;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

/**
 * @author LatvianModder
 */
public class ClientItemTooltipEventJS extends EventJS
{
	private final ItemTooltipEvent event;

	public ClientItemTooltipEventJS(ItemTooltipEvent e)
	{
		event = e;
	}

	public ItemStackJS getItem()
	{
		return ItemStackJS.of(event.getItemStack());
	}

	public void add(@P("text") @T(Text.class) Object text)
	{
		event.getToolTip().add(Text.of(text).component());
	}
}