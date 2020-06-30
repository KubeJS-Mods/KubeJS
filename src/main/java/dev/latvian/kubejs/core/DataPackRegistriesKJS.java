package dev.latvian.kubejs.core;

import dev.latvian.kubejs.server.ServerScriptManager;
import net.minecraft.resources.DataPackRegistries;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IResourcePack;

import java.util.List;

/**
 * @author LatvianModder
 */
public interface DataPackRegistriesKJS
{
	default void initKJS()
	{
		try
		{
			ServerScriptManager.instance = new ServerScriptManager();
			SimpleReloadableResourceManagerKJS manager = (SimpleReloadableResourceManagerKJS) (((DataPackRegistries) this).func_240970_h_());
			IFutureReloadListener reloadListener = ServerScriptManager.instance.createReloadListener();
			manager.getReloadListenersKJS().add(0, reloadListener);
			manager.getInitTaskQueueKJS().add(0, reloadListener);
		}
		catch (Exception ex)
		{
			throw new RuntimeException("KubeJS failed to register it's script loader!");
		}
	}

	static List<IResourcePack> getResourcePackListKJS(List<IResourcePack> list)
	{
		// ...
		return list;
	}
}
