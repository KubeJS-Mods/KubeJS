package dev.latvian.mods.kubejs.mixin.common.inject_resources;

import dev.latvian.mods.kubejs.server.ServerScriptManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {

	@Shadow
	@Final
	private PackRepository packRepository;

	@ModifyVariable(method = {"*"}, at = @At("STORE"), remap = false)
	public CloseableResourceManager wrapResourceManager(CloseableResourceManager original) {
		ServerScriptManager.instance = new ServerScriptManager();
		return ServerScriptManager.instance.wrapResourceManager(original);
	}
}
