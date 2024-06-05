package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.server.ServerScriptManager;
import net.minecraft.tags.TagManager;

public interface ReloadableServerResourcesKJS {
	default ServerScriptManager kjs$getServerScriptManager() {
		throw new NoMixinException();
	}

	default TagManager kjs$getTagManager() {
		throw new NoMixinException();
	}
}
