package dev.latvian.mods.kubejs.neoforge;

import com.mojang.blaze3d.platform.InputConstants;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.bindings.event.ClientEvents;
import dev.latvian.mods.kubejs.bindings.event.ItemEvents;
import dev.latvian.mods.kubejs.block.BlockBuilder;
import dev.latvian.mods.kubejs.client.AtlasSpriteRegistryKubeEvent;
import dev.latvian.mods.kubejs.client.BlockTintFunctionWrapper;
import dev.latvian.mods.kubejs.client.ClientInitKubeEvent;
import dev.latvian.mods.kubejs.client.ItemTintFunctionWrapper;
import dev.latvian.mods.kubejs.client.KubeJSClient;
import dev.latvian.mods.kubejs.fluid.FluidBucketItemBuilder;
import dev.latvian.mods.kubejs.fluid.FluidBuilder;
import dev.latvian.mods.kubejs.gui.KubeJSMenus;
import dev.latvian.mods.kubejs.gui.KubeJSScreen;
import dev.latvian.mods.kubejs.item.ItemBuilder;
import dev.latvian.mods.kubejs.item.ItemModelPropertiesKubeEvent;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.script.ScriptType;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.client.settings.KeyModifier;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = KubeJS.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class KubeJSNeoForgeClient {
	@SubscribeEvent(priority = EventPriority.LOW)
	public static void setupClient(FMLClientSetupEvent event) {
		event.enqueueWork(KubeJSNeoForgeClient::setupClient0);
	}

	private static void setupClient0() {
		ClientEvents.INIT.post(ScriptType.STARTUP, new ClientInitKubeEvent());
		ItemEvents.MODEL_PROPERTIES.post(ScriptType.STARTUP, new ItemModelPropertiesKubeEvent());

		ClientEvents.ATLAS_SPRITE_REGISTRY.listenJava(ScriptType.CLIENT, TextureAtlas.LOCATION_BLOCKS, event -> {
			var e = (AtlasSpriteRegistryKubeEvent) event;

			for (var builder : RegistryInfo.FLUID) {
				if (builder instanceof FluidBuilder b) {
					e.register(b.stillTexture);
					e.register(b.flowingTexture);
				}
			}

			return null;
		});

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
					case "cutout" -> {
						ItemBlockRenderTypes.setRenderLayer(b.get().getSource(), RenderType.cutout());
						ItemBlockRenderTypes.setRenderLayer(b.get().getFlowing(), RenderType.cutout());
					}
					case "cutout_mipped" -> {
						ItemBlockRenderTypes.setRenderLayer(b.get().getSource(), RenderType.cutoutMipped());
						ItemBlockRenderTypes.setRenderLayer(b.get().getFlowing(), RenderType.cutoutMipped());
					}
					case "translucent" -> {
						ItemBlockRenderTypes.setRenderLayer(b.get().getSource(), RenderType.translucent());
						ItemBlockRenderTypes.setRenderLayer(b.get().getFlowing(), RenderType.translucent());
					}
				}
			}
		}
	}

	@SubscribeEvent
	public static void blockColors(RegisterColorHandlersEvent.Block event) {
		for (var builder : RegistryInfo.BLOCK) {
			if (builder instanceof BlockBuilder b && b.tint != null) {
				event.register(new BlockTintFunctionWrapper(b.tint), b.get());
			}
		}
	}

	@SubscribeEvent
	public static void itemColors(RegisterColorHandlersEvent.Item event) {
		for (var builder : RegistryInfo.ITEM) {
			if (builder instanceof ItemBuilder b && b.tint != null) {
				event.register(new ItemTintFunctionWrapper(b.tint), b.get());
			}

			if (builder instanceof FluidBucketItemBuilder b && b.fluidBuilder.bucketColor != 0xFFFFFFFF) {
				event.register((stack, index) -> index == 1 ? b.fluidBuilder.bucketColor : 0xFFFFFFFF, b.get());
			}
		}
	}

	@SubscribeEvent
	public static void registerMenuScreens(RegisterMenuScreensEvent event) {
		event.register(KubeJSMenus.MENU.get(), KubeJSScreen::new);
	}

	@SubscribeEvent
	public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
		event.register(KubeJSClient.guiKey = new KeyMapping("key.kubejs.gui", KeyConflictContext.GUI, KeyModifier.NONE, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_K, KeyMapping.CATEGORY_MISC));
		event.register(KubeJSClient.inGameKey = new KeyMapping("key.kubejs.in_game", KeyConflictContext.IN_GAME, KeyModifier.NONE, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_K, KeyMapping.CATEGORY_MISC));
	}
}
