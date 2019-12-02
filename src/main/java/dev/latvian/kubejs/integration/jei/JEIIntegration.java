package dev.latvian.kubejs.integration.jei;

import dev.latvian.kubejs.documentation.DocumentationEvent;
import net.minecraftforge.common.MinecraftForge;

/**
 * @author LatvianModder
 */
public class JEIIntegration
{
	public static final String JEI_SUBTYPES = "jei.subtypes";
	public static final String JEI_HIDE_ITEMS = "jei.hide.items";
	public static final String JEI_HIDE_FLUIDS = "jei.hide.fluids";
	public static final String JEI_ADD_ITEMS = "jei.add.items";
	public static final String JEI_ADD_FLUIDS = "jei.add.fluids";

	public void init()
	{
		MinecraftForge.EVENT_BUS.addListener(this::registerDocumentation);
	}

	private void registerDocumentation(DocumentationEvent event)
	{
		event.registerEvent(JEI_SUBTYPES, AddJEISubtypesEventJS.class).clientOnly();
		event.registerEvent(JEI_HIDE_ITEMS, HideJEIEventJS.class).clientOnly();
		event.registerEvent(JEI_HIDE_FLUIDS, HideJEIEventJS.class).clientOnly();
		event.registerEvent(JEI_ADD_ITEMS, AddJEIEventJS.class).clientOnly();
		event.registerEvent(JEI_ADD_FLUIDS, AddJEIEventJS.class).clientOnly();
	}
}