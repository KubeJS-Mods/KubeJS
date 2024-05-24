package dev.latvian.mods.kubejs.core.mixin.common.inject_resources;

import dev.latvian.mods.kubejs.server.ServerScriptManager;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
	@Shadow
	public abstract RegistryAccess.Frozen registryAccess();

	@ModifyVariable(method = {"*"}, at = @At("STORE"), remap = false)
	public CloseableResourceManager wrapResourceManager(CloseableResourceManager original) {
		return (ServerScriptManager.instance = new ServerScriptManager(registryAccess())).wrapResourceManager(original);
	}
}
