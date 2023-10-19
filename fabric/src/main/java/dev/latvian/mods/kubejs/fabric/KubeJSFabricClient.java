package dev.latvian.mods.kubejs.fabric;

import dev.latvian.mods.kubejs.bindings.event.ClientEvents;
import dev.latvian.mods.kubejs.block.BlockBuilder;
import dev.latvian.mods.kubejs.client.AtlasSpriteRegistryEventJS;
import dev.latvian.mods.kubejs.client.BlockTintFunctionWrapper;
import dev.latvian.mods.kubejs.client.ItemTintFunctionWrapper;
import dev.latvian.mods.kubejs.fluid.FluidBucketItemBuilder;
import dev.latvian.mods.kubejs.fluid.FluidBuilder;
import dev.latvian.mods.kubejs.item.ItemBuilder;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.minecraft.client.renderer.RenderType;

public class KubeJSFabricClient {
	public static void registry() {
		for (var builder : RegistryInfo.BLOCK) {
			if (builder instanceof BlockBuilder b) {
				switch (b.renderType) {
					case "cutout" -> BlockRenderLayerMap.INSTANCE.putBlocks(RenderType.cutout(), b.get());
					case "cutout_mipped" -> BlockRenderLayerMap.INSTANCE.putBlocks(RenderType.cutoutMipped(), b.get());
					case "translucent" -> BlockRenderLayerMap.INSTANCE.putBlocks(RenderType.translucent(), b.get());
				}

				if (b.tint != null) {
					ColorProviderRegistry.BLOCK.register(new BlockTintFunctionWrapper(b.tint), b.get());
				}
			}
		}

		for (var builder : RegistryInfo.FLUID) {
			if (builder instanceof FluidBuilder b) {
				switch (b.renderType) {
					case "cutout" -> BlockRenderLayerMap.INSTANCE.putFluids(RenderType.cutout(), b.get());
					case "cutout_mipped" -> BlockRenderLayerMap.INSTANCE.putFluids(RenderType.cutoutMipped(), b.get());
					case "translucent" -> BlockRenderLayerMap.INSTANCE.putFluids(RenderType.translucent(), b.get());
				}
			}
		}

		ClientSpriteRegistryCallback.EVENT.register((atlasTexture, registry) -> ClientEvents.ATLAS_SPRITE_REGISTRY.post(new AtlasSpriteRegistryEventJS(registry::register), atlasTexture.location()));

		for (var builder : RegistryInfo.ITEM) {
			if (builder instanceof ItemBuilder b && b.tint != null) {
				ColorProviderRegistry.ITEM.register(new ItemTintFunctionWrapper(b.tint), b.get());
			}

			if (builder instanceof FluidBucketItemBuilder b && b.fluidBuilder.bucketColor != 0xFFFFFFFF) {
				ColorProviderRegistry.ITEM.register((stack, index) -> index == 1 ? b.fluidBuilder.bucketColor : 0xFFFFFFFF, b.get());
			}
		}
	}
}
