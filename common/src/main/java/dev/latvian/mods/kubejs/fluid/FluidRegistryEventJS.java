package dev.latvian.mods.kubejs.fluid;

import dev.latvian.mods.kubejs.KubeJSObjects;
import dev.latvian.mods.kubejs.event.StartupEventJS;

/**
 * @author LatvianModder
 */
public class FluidRegistryEventJS extends StartupEventJS {
	public FluidBuilder create(String name) {
		FluidBuilder builder = new FluidBuilder(name);
		KubeJSObjects.FLUIDS.put(builder.id, builder);
		KubeJSObjects.ALL.add(builder);
		return builder;
	}
}