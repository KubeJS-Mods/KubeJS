package dev.latvian.kubejs.integration;

import dev.latvian.kubejs.integration.gamestages.GameStagesIntegration;
import dev.latvian.kubejs.integration.packmode.PackModeIntegration;
import net.minecraftforge.fml.ModList;

/**
 * @author LatvianModder
 */
public class IntegrationManager
{
	public static void init()
	{
		if (ModList.get().isLoaded("gamestages"))
		{
			new GameStagesIntegration().init();
		}

		if (ModList.get().isLoaded("packmode"))
		{
			new PackModeIntegration().init();
		}
	}
}