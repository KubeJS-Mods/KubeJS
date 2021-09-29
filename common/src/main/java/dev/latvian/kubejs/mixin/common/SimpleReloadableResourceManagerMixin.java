package dev.latvian.kubejs.mixin.common;

import dev.latvian.kubejs.core.SimpleReloadableResourceManagerKJS;
import dev.latvian.kubejs.server.KubeJSReloadListener;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.FallbackResourceManager;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.SimpleReloadableResourceManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author LatvianModder
 */
@Mixin(SimpleReloadableResourceManager.class)
public abstract class SimpleReloadableResourceManagerMixin implements SimpleReloadableResourceManagerKJS {
	@Shadow
	@Final
	private PackType type;

	@Override
	@Accessor("namespacedPacks")
	public abstract Map<String, FallbackResourceManager> getNamespaceResourceManagersKJS();

	@Override
	@Accessor("listeners")
	public abstract List<PreparableReloadListener> getReloadListenersKJS();

	@Override
	@Accessor("recentlyRegistered")
	public abstract List<PreparableReloadListener> getInitTaskQueueKJS();

	@ModifyArg(method = "createFullReload", index = 2, at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/resources/SimpleReloadableResourceManager;createReload(Ljava/util/concurrent/Executor;Ljava/util/concurrent/Executor;Ljava/util/List;Ljava/util/concurrent/CompletableFuture;)Lnet/minecraft/server/packs/resources/ReloadInstance;"))
	private List<PreparableReloadListener> getListenersKJS(List<PreparableReloadListener> old) {
		if (type == PackType.SERVER_DATA) {
			List<PreparableReloadListener> list = new ArrayList<>(old);
			list.add(new KubeJSReloadListener());
			return list;
		}

		return old;
	}
}