package dev.latvian.kubejs.integration.aurora;

/**
 * @author LatvianModder
 */
public class AuroraIntegration
{
	public void init()
	{
		//MinecraftForge.EVENT_BUS.addListener(this::auroraHomePageEvent);
		//MinecraftForge.EVENT_BUS.addListener(this::auroraEvent);
	}
/*
	private void auroraHomePageEvent(AuroraHomePageEvent event)
	{
		event.add(new HomePageEntry("KubeJS Documentation", "kubejs", "https://kubejs.latvian.dev/logo_48.png"));
	}

	private void auroraEvent(AuroraPageEvent event)
	{
		if (event.getSplitUri()[0].equals("kubejs"))
		{
			if (event.getSplitUri().length == 1)
			{
				event.returnPage(new KubeJSHomePage(Documentation.get()));
			}
			else
			{
				try
				{
					Class c = Class.forName(event.getSplitUri()[1]);
					event.returnPage(new KubeJSClassPage(Documentation.get(), c));
				}
				catch (Exception ex)
				{
					event.returnPage(new KubeJSClassErrorPage(event.getSplitUri()[1]));
				}
			}
		}
	}
 */
}