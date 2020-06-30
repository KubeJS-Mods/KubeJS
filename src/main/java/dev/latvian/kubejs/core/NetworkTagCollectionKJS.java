package dev.latvian.kubejs.core;

import net.minecraft.util.registry.Registry;

/**
 * @author LatvianModder
 */
public interface NetworkTagCollectionKJS
{
	<T> Registry<T> getRegistryKJS();
}