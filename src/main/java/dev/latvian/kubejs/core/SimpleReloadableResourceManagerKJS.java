package dev.latvian.kubejs.core;

import net.minecraft.resources.FallbackResourceManager;
import net.minecraft.resources.IFutureReloadListener;

import java.util.List;
import java.util.Map;

/**
 * @author LatvianModder
 */
public interface SimpleReloadableResourceManagerKJS
{
	Map<String, FallbackResourceManager> getNamespaceResourceManagersKJS();

	List<IFutureReloadListener> getReloadListenersKJS();

	List<IFutureReloadListener> getInitTaskQueueKJS();
}