package dev.latvian.kubejs.mixin.common;

import dev.latvian.kubejs.core.SimpleReloadableResourceManagerKJS;
import net.minecraft.server.packs.resources.FallbackResourceManager;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.SimpleReloadableResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Map;

/**
 * @author LatvianModder
 */
@Mixin(SimpleReloadableResourceManager.class)
public abstract class SimpleReloadableResourceManagerMixin implements SimpleReloadableResourceManagerKJS
{
	@Override
	@Accessor("namespacedPacks")
	public abstract Map<String, FallbackResourceManager> getNamespaceResourceManagersKJS();

	@Override
	@Accessor("listeners")
	public abstract List<PreparableReloadListener> getReloadListenersKJS();

	@Override
	@Accessor("recentlyRegistered")
	public abstract List<PreparableReloadListener> getInitTaskQueueKJS();
}