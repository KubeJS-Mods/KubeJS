package dev.latvian.kubejs.integration;

import dev.latvian.kubejs.integration.gamestages.GameStagesIntegration;
import dev.latvian.kubejs.integration.packmode.PackModeIntegration;
import net.minecraftforge.fml.common.Loader;

/**
 * @author LatvianModder
 */
public class IntegrationManager
{
	public static void preInit()
	{
		if (Loader.isModLoaded("gamestages"))
		{
			GameStagesIntegration.preInit();
		}

		if (Loader.isModLoaded("packmode"))
		{
			PackModeIntegration.preInit();
		}
	}
}