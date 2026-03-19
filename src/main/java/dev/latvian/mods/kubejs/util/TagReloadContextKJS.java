package dev.latvian.mods.kubejs.util;

import dev.latvian.mods.kubejs.server.ServerScriptManager;
import org.jspecify.annotations.Nullable;

public class TagReloadContextKJS {
	public static final ThreadLocal<@Nullable ServerScriptManager> CURRENT = new ThreadLocal<>();
}

