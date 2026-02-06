package dev.latvian.mods.kubejs.util;

import dev.latvian.mods.kubejs.core.ReloadableServerResourcesKJS;

public class TagReloadContextKJS {
	public static final ThreadLocal<ReloadableServerResourcesKJS> CURRENT = new ThreadLocal<>();
}

