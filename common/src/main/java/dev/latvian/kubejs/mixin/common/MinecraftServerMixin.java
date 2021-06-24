package dev.latvian.kubejs.mixin.common;

import dev.latvian.kubejs.core.MinecraftServerKJS;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerResources;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * @author LatvianModder
 */
@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin implements MinecraftServerKJS {
	@Override
	@Accessor("resources")
	public abstract ServerResources getServerResourcesKJS();
}
