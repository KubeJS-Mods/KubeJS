package dev.latvian.mods.kubejs.client;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@SuppressWarnings({"rawtypes", "unchecked"})
public class BlockEntityRendererRegistryKubeEvent implements ClientKubeEvent {
	private final EntityRenderersEvent.RegisterRenderers event;

	public BlockEntityRendererRegistryKubeEvent(EntityRenderersEvent.RegisterRenderers event) {
		this.event = event;
	}

	public void register(BlockEntityType<?> type, BlockEntityRendererProvider renderer) {
		event.registerBlockEntityRenderer(type, renderer);
	}
}
