package dev.latvian.mods.kubejs.client;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@SuppressWarnings({"rawtypes", "unchecked"})
public class EntityRendererRegistryKubeEvent implements ClientKubeEvent {
	private final EntityRenderersEvent.RegisterRenderers event;

	public EntityRendererRegistryKubeEvent(EntityRenderersEvent.RegisterRenderers event) {
		this.event = event;
	}

	public void register(EntityType<?> type, EntityRendererProvider renderer) {
		event.registerEntityRenderer(type, renderer);
	}
}
