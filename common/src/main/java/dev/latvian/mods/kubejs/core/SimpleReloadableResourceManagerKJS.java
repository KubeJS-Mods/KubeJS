package dev.latvian.mods.kubejs.core;

import net.minecraft.server.packs.resources.FallbackResourceManager;
import net.minecraft.server.packs.resources.PreparableReloadListener;

import java.util.List;
import java.util.Map;

/**
 * @author LatvianModder
 */
public interface SimpleReloadableResourceManagerKJS {
	Map<String, FallbackResourceManager> getNamespaceResourceManagersKJS();

	List<PreparableReloadListener> getReloadListenersKJS();

	List<PreparableReloadListener> getInitTaskQueueKJS();
}