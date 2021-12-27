package dev.latvian.mods.kubejs.fluid;

import dev.latvian.mods.kubejs.KubeJSObjects;
import dev.latvian.mods.kubejs.event.StartupEventJS;

import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
public class FluidRegistryEventJS extends StartupEventJS {
	public void create(String name, Consumer<FluidBuilder> callback) {
		var builder = new FluidBuilder(name);
		callback.accept(builder);
		KubeJSObjects.FLUIDS.put(builder.id, builder);
		KubeJSObjects.ALL.add(builder);
	}
}