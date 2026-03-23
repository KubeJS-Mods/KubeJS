package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.server.ServerScriptManager;
import org.jspecify.annotations.NullUnmarked;

@NullUnmarked // safe to call from both spots where we need it
public interface ScriptManagerHolderKJS {
	void kjs$setScriptManager(ServerScriptManager manager);

	ServerScriptManager kjs$getScriptManager();
}
