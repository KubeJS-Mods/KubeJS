package dev.latvian.kubejs.core;

import dev.latvian.kubejs.server.ServerScriptManager;
import java.util.List;
import net.minecraft.server.ServerResources;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.resources.PreparableReloadListener;

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
			SimpleReloadableResourceManagerKJS manager = (SimpleReloadableResourceManagerKJS) (((ServerResources) this).getResourceManager());
			PreparableReloadListener reloadListener = ServerScriptManager.instance.createReloadListener();
			manager.getReloadListenersKJS().add(0, reloadListener);
			manager.getInitTaskQueueKJS().add(0, reloadListener);
		}
		catch (Exception ex)
		{
			throw new RuntimeException("KubeJS failed to register it's script loader!");
		}
	}

	static List<PackResources> getResourcePackListKJS(List<PackResources> list)
	{
		// ...
		return list;
	}
}
