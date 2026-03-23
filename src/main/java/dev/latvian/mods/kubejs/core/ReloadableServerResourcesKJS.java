package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.server.ServerScriptManager;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentInitializers;
import org.jspecify.annotations.Nullable;

import java.util.List;

public interface ReloadableServerResourcesKJS {
	@Nullable
	default ServerScriptManager kjs$getServerScriptManager() {
		throw new NoMixinException();
	}

	default void kjs$setServerScriptManager(ServerScriptManager serverScriptManager) {
		throw new NoMixinException();
	}

	default HolderLookup.Provider kjs$getRegistryLookup() {
		throw new NoMixinException();
	}

	default List<Registry.PendingTags<?>> kjs$getPostponedTags() {
		throw new NoMixinException();
	}

	List<DataComponentInitializers.PendingComponents<?>> kjs$getNewComponents();
}
