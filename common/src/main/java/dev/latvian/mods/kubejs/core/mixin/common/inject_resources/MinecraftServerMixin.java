package dev.latvian.mods.kubejs.core.mixin.common.inject_resources;

import dev.latvian.mods.kubejs.server.ServerScriptManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {

	@Shadow
	@Final
	private PackRepository packRepository;

	@SuppressWarnings({"DefaultAnnotationParam"})
	@Redirect(
			method = {"*"},
			at = @At(
					value = "NEW",
					target = "net/minecraft/server/packs/resources/MultiPackResourceManager",
					remap = true
			),
			remap = false
	)
	public MultiPackResourceManager wrapResourceManager(PackType packType, List<PackResources> list) {
		ServerScriptManager.instance = new ServerScriptManager();
		return ServerScriptManager.instance.wrapResourceManager(packType, list);
	}
}
