package dev.latvian.mods.kubejs.mixin.common;

import dev.latvian.mods.kubejs.core.MinecraftServerKJS;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * @author LatvianModder
 */
@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin implements MinecraftServerKJS {
	@Override
	@Accessor("resources")
	public abstract MinecraftServer.ReloadableResources getReloadableResourcesKJS();
}
