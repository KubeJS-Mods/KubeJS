package dev.latvian.kubejs.client;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.event.StartupEventJS;
import dev.latvian.kubejs.util.UtilsJS;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
public class SoundRegistryEventJS extends StartupEventJS {
	private final Consumer<ResourceLocation> registry;

	public SoundRegistryEventJS(Consumer<ResourceLocation> registry) {
		this.registry = registry;
	}

	public void register(String id) {
		ResourceLocation r = UtilsJS.getMCID(KubeJS.appendModId(id));
		registry.accept(r);
	}
}