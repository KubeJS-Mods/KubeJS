package dev.latvian.mods.kubejs.forge;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.bindings.event.ClientEvents;
import dev.latvian.mods.kubejs.block.BlockBuilder;
import dev.latvian.mods.kubejs.client.AtlasSpriteRegistryEventJS;
import dev.latvian.mods.kubejs.fluid.FluidBucketItemBuilder;
import dev.latvian.mods.kubejs.fluid.FluidBuilder;
import dev.latvian.mods.kubejs.item.ItemBuilder;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.script.ScriptType;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class KubeJSForgeClient {
	public KubeJSForgeClient() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(EventPriority.LOW, this::setupClient);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::blockColors);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::itemColors);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::textureStitch);
	}

	@SuppressWarnings("removal")
	private void setupClient(FMLClientSetupEvent event) {
		KubeJS.PROXY.clientSetup();

		for (var builder : RegistryInfo.BLOCK) {
			if (builder instanceof BlockBuilder b) {
				switch (b.renderType) {
					case "cutout" -> ItemBlockRenderTypes.setRenderLayer(b.get(), RenderType.cutout());
					case "cutout_mipped" -> ItemBlockRenderTypes.setRenderLayer(b.get(), RenderType.cutoutMipped());
					case "translucent" -> ItemBlockRenderTypes.setRenderLayer(b.get(), RenderType.translucent());
				}
			}
		}

		for (var builder : RegistryInfo.FLUID) {
			if (builder instanceof FluidBuilder b) {
				switch (b.renderType) {
					case "cutout" -> ItemBlockRenderTypes.setRenderLayer(b.get(), RenderType.cutout());
					case "cutout_mipped" -> ItemBlockRenderTypes.setRenderLayer(b.get(), RenderType.cutoutMipped());
					case "translucent" -> ItemBlockRenderTypes.setRenderLayer(b.get(), RenderType.translucent());
				}
			}
		}
	}

	private void blockColors(RegisterColorHandlersEvent.Block event) {
		for (var builder : RegistryInfo.BLOCK) {
			if (builder instanceof BlockBuilder b && !b.color.isEmpty()) {
				event.register((state, level, pos, index) -> b.color.get(index), b.get());
			}
		}
	}

	private void itemColors(RegisterColorHandlersEvent.Item event) {
		for (var builder : RegistryInfo.ITEM) {
			if (builder instanceof ItemBuilder b && b.colorCallback != null) {
				event.register((stack, tintIndex) -> b.colorCallback.getColor(stack, tintIndex).getArgbJS(), b.get());
			}

			if (builder instanceof FluidBucketItemBuilder b && b.fluidBuilder.bucketColor != 0xFFFFFFFF) {
				event.register((stack, index) -> index == 1 ? b.fluidBuilder.bucketColor : 0xFFFFFFFF, b.get());
			}
		}
	}

	private void textureStitch(TextureStitchEvent.Pre event) {
		ClientEvents.ATLAS_SPRITE_REGISTRY.post(ScriptType.CLIENT, event.getAtlas().location(), new AtlasSpriteRegistryEventJS(event::addSprite));
	}
}
