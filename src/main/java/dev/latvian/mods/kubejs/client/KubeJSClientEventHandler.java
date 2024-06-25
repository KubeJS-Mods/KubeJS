package dev.latvian.mods.kubejs.client;

import com.mojang.serialization.DynamicOps;
import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.bindings.TextIcons;
import dev.latvian.mods.kubejs.bindings.event.ClientEvents;
import dev.latvian.mods.kubejs.bindings.event.ItemEvents;
import dev.latvian.mods.kubejs.item.ItemTooltipKubeEvent;
import dev.latvian.mods.kubejs.kubedex.KubedexHighlight;
import dev.latvian.mods.kubejs.script.ConsoleJS;
import dev.latvian.mods.kubejs.script.ScriptType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.CustomizeGuiOverlayEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import org.jetbrains.annotations.Nullable;

import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@EventBusSubscriber(modid = KubeJS.MOD_ID, value = Dist.CLIENT)
public class KubeJSClientEventHandler {
	public static Map<Item, List<ItemTooltipKubeEvent.StaticTooltipHandler>> staticItemTooltips = null;

	@SubscribeEvent
	public static void debugInfo(CustomizeGuiOverlayEvent.DebugText event) {
		var mc = Minecraft.getInstance();

		if (mc.player != null) {
			if (ClientEvents.DEBUG_LEFT.hasListeners()) {
				ClientEvents.DEBUG_LEFT.post(new DebugInfoKubeEvent(mc.player, event.getLeft()));
			}
			if (ClientEvents.DEBUG_RIGHT.hasListeners()) {
				ClientEvents.DEBUG_RIGHT.post(new DebugInfoKubeEvent(mc.player, event.getRight()));
			}
		}
	}

	private static <T> void appendComponentValue(DynamicOps<Tag> ops, MutableComponent line, DataComponentType<T> type, T value) {
		if (value == null) {
			line.append(Component.literal("null").kjs$red());
			return;
		} else if (value instanceof Component c) {
			line.append(Component.empty().kjs$green().append(c));
		}

		try {
			var tag = type.codecOrThrow().encodeStart(ops, value).getOrThrow();
			line.append(NbtUtils.toPrettyComponent(tag));
		} catch (Exception ex) {
			line.append(Component.literal(String.valueOf(value)).kjs$green());
		}
	}

	@SubscribeEvent
	public static void onItemTooltip(ItemTooltipEvent event) {
		var mc = Minecraft.getInstance();
		var stack = event.getItemStack();
		var lines = event.getToolTip();
		var flag = event.getFlags();

		if (stack.isEmpty()) {
			return;
		}

		var advanced = flag.isAdvanced();

		if (mc.level != null && advanced && ClientProperties.get().showComponents && Screen.hasAltDown()) {
			var components = BuiltInRegistries.DATA_COMPONENT_TYPE;
			var ops = mc.level.registryAccess().createSerializationContext(NbtOps.INSTANCE);

			for (var entry : stack.getComponentsPatch().entrySet()) {
				var id = components.getKey(entry.getKey());

				if (id != null) {
					var line = Component.empty();
					line.append(TextIcons.icon(Component.literal("Q.")));

					if (entry.getValue().isEmpty()) {
						line.append(Component.literal("!"));
					}

					line.append(Component.literal(id.getNamespace().equals("minecraft") ? id.getPath() : id.toString()).kjs$yellow());

					if (entry.getValue().isPresent()) {
						line.append(Component.literal("="));
						appendComponentValue(ops, line, (DataComponentType) entry.getKey(), entry.getValue().get());
					}

					lines.add(line);
				}
			}

			if (Screen.hasShiftDown()) {
				for (var type : stack.getPrototype()) {
					var id = components.getKey(type.type());

					if (id != null && stack.getComponentsPatch().get(type.type()) == null) {
						var line = Component.empty();
						line.append(TextIcons.icon(Component.literal("P.")));
						line.append(Component.literal(id.getNamespace().equals("minecraft") ? id.getPath() : id.toString()).kjs$gray());
						line.append(Component.literal("="));
						appendComponentValue(ops, line, (DataComponentType) type.type(), type.value());
						lines.add(line);
					}
				}
			}
		} else if (advanced && ClientProperties.get().showTagNames && Screen.hasShiftDown()) {
			var tempTagNames = new LinkedHashMap<ResourceLocation, TagInstance>();
			TagInstance.Type.ITEM.append(tempTagNames, stack.getItem().builtInRegistryHolder().tags());

			if (stack.getItem() instanceof BlockItem item) {
				TagInstance.Type.BLOCK.append(tempTagNames, item.getBlock().builtInRegistryHolder().tags());
			}

			if (stack.getItem() instanceof BucketItem bucket) {
				Fluid fluid = bucket.content;

				if (fluid != Fluids.EMPTY) {
					TagInstance.Type.FLUID.append(tempTagNames, fluid.builtInRegistryHolder().tags());
				}
			}

			if (stack.getItem() instanceof SpawnEggItem item) {
				var entityType = item.getType(stack);

				if (entityType != null) {
					TagInstance.Type.ENTITY.append(tempTagNames, entityType.builtInRegistryHolder().tags());
				}
			}

			for (var instance : tempTagNames.values()) {
				lines.add(instance.toText());
			}
		}

		if (staticItemTooltips == null) {
			staticItemTooltips = new IdentityHashMap<>();
			ItemEvents.TOOLTIP.post(ScriptType.CLIENT, new ItemTooltipKubeEvent(staticItemTooltips));
		}

		try {
			for (var handler : staticItemTooltips.getOrDefault(Items.AIR, List.of())) {
				handler.tooltip(stack, advanced, lines);
			}
		} catch (Exception ex) {
			ConsoleJS.CLIENT.error("Error while gathering tooltip for " + stack, ex);
		}

		try {
			for (var handler : staticItemTooltips.getOrDefault(stack.getItem(), List.of())) {
				handler.tooltip(stack, advanced, lines);
			}
		} catch (Exception ex) {
			ConsoleJS.CLIENT.error("Error while gathering tooltip for " + stack, ex);
		}
	}

	@SubscribeEvent
	public static void loggingIn(ClientPlayerNetworkEvent.LoggingIn event) {
		ClientEvents.LOGGED_IN.post(ScriptType.CLIENT, new ClientPlayerKubeEvent(event.getPlayer()));
	}

	@SubscribeEvent
	public static void loggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
		ClientEvents.LOGGED_OUT.post(ScriptType.CLIENT, new ClientPlayerKubeEvent(event.getPlayer()));
	}

	@SubscribeEvent
	public static void hudPostDraw(RenderGuiEvent.Post event) {
		var mc = Minecraft.getInstance();

		if (mc.screen == null) {
			KubedexHighlight.INSTANCE.afterEverything(mc, event.getGuiGraphics(), event.getPartialTick().getGameTimeDeltaPartialTick(false));
		}
	}

	@SubscribeEvent
	public static void screenPostDraw(ScreenEvent.Render.Post event) {
		var mc = Minecraft.getInstance();

		if (event.getScreen() instanceof AbstractContainerScreen<?> screen) {
			KubedexHighlight.INSTANCE.screen(mc, event.getGuiGraphics(), screen, event.getMouseX(), event.getMouseY(), event.getPartialTick());
		}

		KubedexHighlight.INSTANCE.afterEverything(mc, event.getGuiGraphics(), event.getPartialTick());
	}

	@SubscribeEvent
	public static void clientTick(ClientTickEvent.Pre event) {
		var mc = Minecraft.getInstance();
		KubedexHighlight.INSTANCE.tickPre(mc);
	}

	@SubscribeEvent
	public static void worldRender(RenderLevelStageEvent event) {
		var mc = Minecraft.getInstance();

		if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_SKY) {
			KubedexHighlight.INSTANCE.clearBuffers(mc);
		} else if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_ENTITIES) {
			KubedexHighlight.INSTANCE.renderAfterEntities(mc, event);
		} else if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_LEVEL) {
			var depth = KubedexHighlight.INSTANCE.mcDepthInput;

			if (depth != null) {
				depth.bindWrite(false);
				depth.clear(Minecraft.ON_OSX);
				depth.copyDepthFrom(mc.getMainRenderTarget());
				mc.getMainRenderTarget().bindWrite(false);
			}
		}
	}

	@Nullable
	public static Screen setScreen(Screen screen) {
		if (screen instanceof TitleScreen && !ConsoleJS.STARTUP.errors.isEmpty() && CommonProperties.get().startupErrorGUI) {
			return new KubeJSErrorScreen(screen, ConsoleJS.STARTUP, false);
		}

		if (screen instanceof TitleScreen && !ConsoleJS.CLIENT.errors.isEmpty() && CommonProperties.get().startupErrorGUI) {
			return new KubeJSErrorScreen(screen, ConsoleJS.CLIENT, false);
		}

		return screen;
	}

	@SubscribeEvent
	public static void guiPostInit(ScreenEvent.Init.Post event) {
		var screen = event.getScreen();

		if (ClientProperties.get().disableRecipeBook && screen instanceof RecipeUpdateListener) {
			var iterator = screen.children().iterator();
			while (iterator.hasNext()) {
				var listener = iterator.next();
				if (listener instanceof ImageButton button && button.sprites.enabled().equals(KubeJSClient.RECIPE_BUTTON_TEXTURE)) {
					screen.renderables.remove(listener);
					screen.narratables.remove(listener);
					iterator.remove();
					return;
				}
			}
		}
	}

	/*private void postAtlasStitch(TextureAtlas atlas) {
		if (!ClientProperties.get().getExportAtlases()) {
			return;
		}

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, atlas.getId());
		var w = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
		var h = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);

		if (w <= 0 || h <= 0) {
			return;
		}

		var image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		var pixels = new int[w * h];

		var result = BufferUtils.createIntBuffer(w * h);
		GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, result);
		result.get(pixels);

		image.setRGB(0, 0, w, h, pixels, 0, w);

		var path = KubeJSPaths.EXPORT.resolve(atlas.location().getNamespace() + "/" + atlas.location().getPath());

		if (!Files.exists(path.getParent())) {
			try {
				Files.createDirectories(path.getParent());
			} catch (Exception ex) {
				ex.printStackTrace();
				return;
			}
		}

		if (!Files.exists(path)) {
			try {
				Files.createFile(path);
			} catch (Exception ex) {
				ex.printStackTrace();
				return;
			}
		}

		try (var stream = Files.newOutputStream(path)) {
			ImageIO.write(image, "PNG", stream);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}*/

	// FIXME: implement
	/*private void textureStitch(TextureStitchEvent.Pre event) {
		ClientEvents.ATLAS_SPRITE_REGISTRY.post(new AtlasSpriteRegistryEventJS(event::addSprite), event.getAtlas().location());
	}*/

	@SubscribeEvent(priority = EventPriority.LOW)
	public static void openScreenEvent(ScreenEvent.Opening event) {
		var s = KubeJSClientEventHandler.setScreen(event.getScreen());

		if (s != null && event.getScreen() != s) {
			event.setNewScreen(s);
		}
	}
}