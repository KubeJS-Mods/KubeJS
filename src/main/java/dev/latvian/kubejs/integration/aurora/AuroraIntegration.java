package dev.latvian.kubejs.integration.aurora;

import dev.latvian.kubejs.documentation.Documentation;
import dev.latvian.mods.aurora.AuroraHomePageEvent;
import dev.latvian.mods.aurora.AuroraPageEvent;
import dev.latvian.mods.aurora.page.HomePageEntry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author LatvianModder
 */
public class AuroraIntegration
{
	public static void init()
	{
		MinecraftForge.EVENT_BUS.register(AuroraIntegration.class);
	}

	@SubscribeEvent
	public static void onAuroraHomePageEvent(AuroraHomePageEvent event)
	{
		event.add(new HomePageEntry("KubeJS Documentation", "kubejs", "https://kubejs.latvian.dev/logo_48.png"));
	}

	@SubscribeEvent
	public static void onAuroraEvent(AuroraPageEvent event)
	{
		if (event.getSplitUri()[0].equals("kubejs"))
		{
			if (event.getSplitUri().length == 1)
			{
				event.setPage(new KubeJSHomePage(Documentation.get()));
			}
			else
			{
				try
				{
					Class c = Class.forName(event.getSplitUri()[1]);
					event.setPage(new KubeJSClassPage(Documentation.get(), c));
				}
				catch (Exception ex)
				{
					event.setPage(new KubeJSClassErrorPage(event.getSplitUri()[1]));
				}
			}

			event.setCanceled(true);
		}
	}
}