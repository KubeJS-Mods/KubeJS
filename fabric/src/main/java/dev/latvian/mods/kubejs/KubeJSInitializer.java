package dev.latvian.mods.kubejs;

import org.jetbrains.annotations.ApiStatus;

/**
 * @deprecated Use {@link KubeJSPlugin}s instead.
 * This API may change or even be removed entirely in the future.
 */
@Deprecated
@ApiStatus.Experimental
@FunctionalInterface
public interface KubeJSInitializer {
	void onKubeJSInitialization();
}