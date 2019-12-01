package dev.latvian.kubejs.integration.jei;

import dev.latvian.kubejs.documentation.DocumentationEvent;
import net.minecraftforge.common.MinecraftForge;

/**
 * @author LatvianModder
 */
public class JEIIntegration
{
	public static final String JEI_SUBTYPES = "jei.subtypes";

	public void init()
	{
		MinecraftForge.EVENT_BUS.addListener(this::registerDocumentation);
	}

	private void registerDocumentation(DocumentationEvent event)
	{
		event.registerEvent(JEI_SUBTYPES, AddJEISubtypesEventJS.class).startup();
	}
}