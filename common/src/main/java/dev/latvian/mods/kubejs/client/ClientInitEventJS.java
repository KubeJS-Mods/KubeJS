package dev.latvian.mods.kubejs.client;

import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import dev.architectury.registry.menu.MenuRegistry;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ClientInitEventJS extends ClientEventJS {
	public void registerBlockEntityRenderer(BlockEntityType<?> type, BlockEntityRendererProvider renderer) {
		BlockEntityRendererRegistry.register(type, renderer);
	}

	public void registerEntityRenderer(EntityType<?> type, EntityRendererProvider renderer) {
		EntityRendererRegistry.register(() -> type, renderer);
	}

	public void registerMenuScreen(MenuType<?> type, MenuRegistry.ScreenFactory screenFactory) {
		MenuRegistry.registerScreenFactory(type, screenFactory);
	}
}
