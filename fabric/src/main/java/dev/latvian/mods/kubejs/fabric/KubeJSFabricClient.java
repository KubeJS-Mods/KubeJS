package dev.latvian.mods.kubejs.fabric;

import dev.architectury.registry.client.rendering.ColorHandlerRegistry;
import dev.architectury.registry.client.rendering.RenderTypeRegistry;
import dev.latvian.mods.kubejs.block.BlockBuilder;
import dev.latvian.mods.kubejs.fluid.FluidBucketItemBuilder;
import dev.latvian.mods.kubejs.item.ItemBuilder;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import net.minecraft.client.renderer.RenderType;

public class KubeJSFabricClient {
	public static void registry() {
		for (var builder : RegistryInfo.BLOCK) {
			if (builder instanceof BlockBuilder b) {
				var block = b.get();

				switch (b.renderType) {
					case "cutout" -> RenderTypeRegistry.register(RenderType.cutout(), block);
					case "cutout_mipped" -> RenderTypeRegistry.register(RenderType.cutoutMipped(), block);
					case "translucent" -> RenderTypeRegistry.register(RenderType.translucent(), block);
				}
			}
		}

		for (var builder : RegistryInfo.BLOCK) {
			if (builder instanceof BlockBuilder b && !b.color.isEmpty()) {
				ColorHandlerRegistry.registerBlockColors((state, level, pos, index) -> b.color.get(index), b);
			}
		}

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
