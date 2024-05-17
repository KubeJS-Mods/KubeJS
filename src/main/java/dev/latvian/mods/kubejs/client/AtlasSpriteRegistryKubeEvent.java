package dev.latvian.mods.kubejs.client;

import dev.latvian.mods.kubejs.event.KubeEvent;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public class AtlasSpriteRegistryKubeEvent implements KubeEvent {
	private final Consumer<ResourceLocation> registry;

	public AtlasSpriteRegistryKubeEvent(Consumer<ResourceLocation> registry) {
		this.registry = registry;
	}

	public void register(ResourceLocation id) {
		registry.accept(id);
	}
}
