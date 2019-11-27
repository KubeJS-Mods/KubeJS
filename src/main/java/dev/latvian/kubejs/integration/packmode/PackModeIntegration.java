package dev.latvian.kubejs.integration.packmode;

import dev.latvian.kubejs.script.BindingsEvent;
import net.minecraftforge.common.MinecraftForge;

/**
 * @author LatvianModder
 */
public class PackModeIntegration
{
	public void init()
	{
		MinecraftForge.EVENT_BUS.addListener(this::registerBindings);
	}

	private void registerBindings(BindingsEvent event)
	{
		event.add("packmode", new PackModeWrapper());
	}
}