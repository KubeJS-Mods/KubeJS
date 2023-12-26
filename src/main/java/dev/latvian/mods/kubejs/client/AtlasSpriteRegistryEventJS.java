package dev.latvian.mods.kubejs.client;

import dev.latvian.mods.kubejs.event.EventJS;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public class AtlasSpriteRegistryEventJS extends EventJS {
	private final Consumer<ResourceLocation> registry;

	public AtlasSpriteRegistryEventJS(Consumer<ResourceLocation> registry) {
		this.registry = registry;
	}

	public void register(ResourceLocation id) {
		registry.accept(id);
	}
}
