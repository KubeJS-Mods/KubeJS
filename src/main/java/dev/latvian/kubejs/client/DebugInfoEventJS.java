package dev.latvian.kubejs.client;

import dev.latvian.kubejs.event.EventJS;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import java.util.List;

/**
 * @author LatvianModder
 */
public class DebugInfoEventJS extends EventJS
{
	private final RenderGameOverlayEvent.Text event;

	public DebugInfoEventJS(RenderGameOverlayEvent.Text e)
	{
		event = e;
	}

	public boolean getShowDebug()
	{
		return Minecraft.getInstance().gameSettings.showDebugInfo;
	}

	public List<String> getLeft()
	{
		return event.getLeft();
	}

	public List<String> getRight()
	{
		return event.getRight();
	}
}