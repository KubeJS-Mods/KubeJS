package dev.latvian.kubejs.integration.packmode;

import dev.latvian.kubejs.script.BindingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author LatvianModder
 */
public class PackModeIntegration
{
	public static void preInit()
	{
		MinecraftForge.EVENT_BUS.register(PackModeIntegration.class);
	}

	@SubscribeEvent
	public static void registerBindings(BindingsEvent event)
	{
		event.add("packmode", new PackModeWrapper());
	}
}