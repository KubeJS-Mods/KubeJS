package dev.latvian.mods.kubejs.client;

import dev.architectury.registry.client.level.entity.forge.EntityRendererRegistryImpl;
import dev.architectury.registry.client.rendering.forge.BlockEntityRendererRegistryImpl;
import dev.architectury.registry.menu.MenuRegistry;
import dev.architectury.registry.menu.forge.MenuRegistryImpl;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntityType;

@SuppressWarnings({"rawtypes", "unchecked"})
public class ClientInitKubeEvent extends ClientKubeEvent {
	public void registerBlockEntityRenderer(BlockEntityType<?> type, BlockEntityRendererProvider renderer) {
		BlockEntityRendererRegistryImpl.register(type, renderer);
	}

	public void registerEntityRenderer(EntityType<?> type, EntityRendererProvider renderer) {
		EntityRendererRegistryImpl.register(() -> type, renderer);
	}

	public void registerMenuScreen(MenuType<?> type, MenuRegistry.ScreenFactory screenFactory) {
		MenuRegistryImpl.registerScreenFactory(type, screenFactory);
	}
}
