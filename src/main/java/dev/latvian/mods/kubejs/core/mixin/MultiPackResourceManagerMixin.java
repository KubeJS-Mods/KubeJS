package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.core.ScriptManagerHolderKJS;
import dev.latvian.mods.kubejs.server.ServerScriptManager;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(MultiPackResourceManager.class)
public class MultiPackResourceManagerMixin implements ScriptManagerHolderKJS {
	@Unique
	private ServerScriptManager kjs$pendingServerScriptManager;

	@Override
	public void kjs$setScriptManager(ServerScriptManager manager) {
		kjs$pendingServerScriptManager = manager;
	}

	@Override
	public ServerScriptManager kjs$getScriptManager() {
		return kjs$pendingServerScriptManager;
	}
}
