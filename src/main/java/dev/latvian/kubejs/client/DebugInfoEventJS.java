package dev.latvian.kubejs.client;

import dev.latvian.kubejs.event.EventJS;
import net.minecraft.client.Minecraft;

import java.util.List;

/**
 * @author LatvianModder
 */
public class DebugInfoEventJS extends EventJS
{
	private final List<String> lines;

	public DebugInfoEventJS(List<String> l)
	{
		lines = l;
	}

	public boolean getShowDebug()
	{
		return Minecraft.getInstance().gameSettings.showDebugInfo;
	}

	public List<String> getLines()
	{
		return lines;
	}
}