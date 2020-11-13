package dev.latvian.kubejs.mixin.forge;

import dev.latvian.kubejs.core.SimpleReloadableResourceManagerKJS;
import net.minecraft.resources.FallbackResourceManager;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.SimpleReloadableResourceManager;
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
	public abstract List<IFutureReloadListener> getReloadListenersKJS();

	@Override
	@Accessor("recentlyRegistered")
	public abstract List<IFutureReloadListener> getInitTaskQueueKJS();
}