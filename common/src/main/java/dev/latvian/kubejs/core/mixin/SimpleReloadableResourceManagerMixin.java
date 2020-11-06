package dev.latvian.kubejs.core.mixin;

import dev.latvian.kubejs.core.SimpleReloadableResourceManagerKJS;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Map;
import net.minecraft.server.packs.resources.FallbackResourceManager;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.SimpleReloadableResourceManager;

/**
 * @author LatvianModder
 */
@Mixin(SimpleReloadableResourceManager.class)
public abstract class SimpleReloadableResourceManagerMixin implements SimpleReloadableResourceManagerKJS
{
	@Override
	@Accessor("namespaceResourceManagers")
	public abstract Map<String, FallbackResourceManager> getNamespaceResourceManagersKJS();

	@Override
	@Accessor("reloadListeners")
	public abstract List<PreparableReloadListener> getReloadListenersKJS();

	@Override
	@Accessor("initTaskQueue")
	public abstract List<PreparableReloadListener> getInitTaskQueueKJS();
}