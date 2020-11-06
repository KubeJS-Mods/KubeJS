package dev.latvian.kubejs.core;

import java.util.List;
import java.util.Map;
import net.minecraft.server.packs.resources.FallbackResourceManager;
import net.minecraft.server.packs.resources.PreparableReloadListener;

/**
 * @author LatvianModder
 */
public interface SimpleReloadableResourceManagerKJS
{
	Map<String, FallbackResourceManager> getNamespaceResourceManagersKJS();

	List<PreparableReloadListener> getReloadListenersKJS();

	List<PreparableReloadListener> getInitTaskQueueKJS();
}