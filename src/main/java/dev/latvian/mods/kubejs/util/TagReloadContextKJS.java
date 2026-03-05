package dev.latvian.mods.kubejs.util;

import dev.latvian.mods.kubejs.server.ServerScriptManager;

public class TagReloadContextKJS {
	public static final ThreadLocal<ServerScriptManager> CURRENT = new ThreadLocal<>();
}

