package dev.latvian.kubejs.fabric;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.KubeJSInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.Level;

public class KubeJSFabric implements ModInitializer
{
	@Override
	public void onInitialize()
	{
		try
		{
			KubeJS kubeJS = new KubeJS();
			FabricLoader.getInstance().getEntrypoints("kubejs-init", KubeJSInitializer.class).forEach((it) -> {
				it.onKubeJSInitialization();
				KubeJS.LOGGER.log(Level.DEBUG, "[KubeJS] Initialized entrypoint " + it.getClass().getSimpleName() + ".");
			});
			kubeJS.setup();
			kubeJS.loadComplete();
		}
		catch (Throwable throwable)
		{
			throw new RuntimeException(throwable);
		}
	}
}
