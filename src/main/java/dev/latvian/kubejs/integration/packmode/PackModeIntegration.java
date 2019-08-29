package dev.latvian.kubejs.integration.packmode;

import dev.latvian.kubejs.KubeJSBindingsEvent;
import dev.latvian.kubejs.documentation.DocumentationEvent;
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
	public static void registerDocumentation(DocumentationEvent event)
	{
		event.register(PackModeWrapper.class);
	}

	@SubscribeEvent
	public static void registerBindings(KubeJSBindingsEvent event)
	{
		event.add("packmode", new PackModeWrapper());
	}
}