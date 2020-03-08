package dev.latvian.kubejs.client;

import dev.latvian.kubejs.event.EventJS;
import net.minecraft.client.Minecraft;

/**
 * @author LatvianModder
 */
public class ClientInitEventJS extends EventJS
{
	public void setTitle(String title)
	{
		Minecraft.getInstance().getMainWindow().func_230148_b_(title);
	}
}