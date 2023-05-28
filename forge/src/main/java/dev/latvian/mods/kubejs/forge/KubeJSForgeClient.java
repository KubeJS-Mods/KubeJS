package dev.latvian.mods.kubejs.forge;

import dev.architectury.registry.client.rendering.ColorHandlerRegistry;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.block.BlockBuilder;
import dev.latvian.mods.kubejs.fluid.FluidBucketItemBuilder;
import dev.latvian.mods.kubejs.item.ItemBuilder;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class KubeJSForgeClient {
	public KubeJSForgeClient() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(EventPriority.LOW, this::setupClient);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::blockColors);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::itemColors);
	}

	@SuppressWarnings("removal")
	private void setupClient(FMLClientSetupEvent event) {
		KubeJS.PROXY.clientSetup();

		for (var builder : RegistryInfo.BLOCK) {
			if (builder instanceof BlockBuilder b) {
				Block block = b.getObject();

				switch (b.renderType) {
					case "cutout" -> ItemBlockRenderTypes.setRenderLayer(block, RenderType.cutout());
					case "cutout_mipped" -> ItemBlockRenderTypes.setRenderLayer(block, RenderType.cutoutMipped());
					case "translucent" -> ItemBlockRenderTypes.setRenderLayer(block, RenderType.translucent());
				}
			}
		}
	}

	private void blockColors(RegisterColorHandlersEvent.Block event) {
		for (var builder : RegistryInfo.BLOCK) {
			if (builder instanceof BlockBuilder b && !b.color.isEmpty()) {
				ColorHandlerRegistry.registerBlockColors((state, level, pos, index) -> state.getBlock().kjs$getBlockBuilder().color.get(index), b);
			}
		}
	}

	private void itemColors(RegisterColorHandlersEvent.Item event) {
		for (var builder : RegistryInfo.ITEM) {
			if (builder instanceof ItemBuilder b && b.colorCallback != null) {
				ColorHandlerRegistry.registerItemColors((stack, tintIndex) -> b.colorCallback.getColor(stack, tintIndex).getArgbJS(), b);
			}

			if (builder instanceof FluidBucketItemBuilder b && b.fluidBuilder.bucketColor != 0xFFFFFFFF) {
				ColorHandlerRegistry.registerItemColors((stack, index) -> index == 1 ? b.fluidBuilder.bucketColor : 0xFFFFFFFF, b);
			}
		}
	}
}
