package dev.latvian.mods.kubejs.mixin.common;

import dev.latvian.mods.kubejs.core.SimpleReloadableResourceManagerKJS;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.FallbackResourceManager;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.SimpleReloadableResourceManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;

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
}