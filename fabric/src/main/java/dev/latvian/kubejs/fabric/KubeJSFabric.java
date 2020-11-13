package dev.latvian.kubejs.fabric;

import dev.latvian.kubejs.KubeJS;
import net.fabricmc.api.ModInitializer;

public class KubeJSFabric implements ModInitializer
{
	@Override
	public void onInitialize()
	{
		try
		{
			KubeJS kubeJS = new KubeJS();
			kubeJS.setup();
			kubeJS.loadComplete();
		}
		catch (Throwable throwable)
		{
			throw new RuntimeException(throwable);
		}
	}
}
