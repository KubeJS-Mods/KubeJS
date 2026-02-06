package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.server.ServerScriptManager;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;

import java.util.List;

public interface ReloadableServerResourcesKJS {
	default ServerScriptManager kjs$getServerScriptManager() {
		throw new NoMixinException();
	}

	default HolderLookup.Provider kjs$getRegistryLookup() {
		throw new NoMixinException();
	}

	// TagManager removed in favor of a list of PendingTags
	default List<Registry.PendingTags<?>> kjs$getPostponedTags() {
		throw new NoMixinException();
	}
}
