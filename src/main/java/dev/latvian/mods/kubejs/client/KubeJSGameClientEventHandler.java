package dev.latvian.mods.kubejs.client;

import com.mojang.serialization.DynamicOps;
import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.bindings.TextIcons;
import dev.latvian.mods.kubejs.bindings.event.ClientEvents;
import dev.latvian.mods.kubejs.bindings.event.ItemEvents;
import dev.latvian.mods.kubejs.item.DynamicItemTooltipsKubeEvent;
import dev.latvian.mods.kubejs.kubedex.KubedexHighlight;
import dev.latvian.mods.kubejs.script.ConsoleJS;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.text.action.DynamicTextAction;
import dev.latvian.mods.kubejs.text.tooltip.ItemTooltipData;
import dev.latvian.mods.kubejs.text.tooltip.TooltipRequirements;
import dev.latvian.mods.kubejs.util.ID;
import dev.latvian.mods.kubejs.util.StackTraceCollector;
import dev.latvian.mods.kubejs.util.Tristate;
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
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Pattern;

@EventBusSubscriber(modid = KubeJS.MOD_ID, value = Dist.CLIENT)
public class KubeJSGameClientEventHandler {
	public static final Pattern COMPONENT_ERROR = ConsoleJS.methodPattern(KubeJSGameClientEventHandler.class, "onItemTooltip");
	private static List<String> lastComponentError = List.of();

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

	private static <T> List<String> appendComponentValue(DynamicOps<Tag> ops, MutableComponent line, DataComponentType<T> type, T value) {
		if (value == null) {
			line.append(Component.literal("null").kjs$red());
			return List.of();
		} else if (value instanceof Component c) {
			line.append(Component.empty().kjs$gold().append(c));
		}

		try {
			var tag = type.codecOrThrow().encodeStart(ops, value).getOrThrow();
			line.append(NbtUtils.toPrettyComponent(tag));
			return List.of();
		} catch (Throwable ex) {
			line.append(Component.literal(String.valueOf(value)).kjs$red());
			var lines = new ArrayList<String>();
			ex.printStackTrace(new StackTraceCollector(lines, COMPONENT_ERROR, ConsoleJS.ERROR_REDUCE));
			return lines;
		}
	}

	public static boolean testRequirements(Minecraft mc, DynamicItemTooltipsKubeEvent event, TooltipRequirements r) {
		if (!r.advanced().test(event.advanced)) {
			return false;
		}

		if (!r.creative().test(event.creative)) {
			return false;
		}

		if (!r.shift().test(event.shift)) {
			return false;
		}

		if (!r.ctrl().test(event.ctrl)) {
			return false;
		}

		if (!r.alt().test(event.alt)) {
			return false;
		}

		if (!r.stages().isEmpty()) {
			var stages = mc.player.kjs$getStages();

			for (var entry : r.stages().entrySet()) {
				if (entry.getValue() != Tristate.DEFAULT && !entry.getValue().test(stages.has(entry.getKey()))) {
					return false;
				}
			}
		}

		return true;
	}

	private static void handleItemTooltips(Minecraft mc, ItemTooltipData tooltip, DynamicItemTooltipsKubeEvent event) {
		if ((tooltip.filter().isEmpty() || tooltip.filter().get().test(event.item)) && (tooltip.requirements().isEmpty() || testRequirements(mc, event, tooltip.requirements().get()))) {
			for (var action : tooltip.actions()) {
				if (action instanceof DynamicTextAction dynamic) {
					try {
						ItemEvents.DYNAMIC_TOOLTIPS.post(ScriptType.CLIENT, dynamic.id(), event);
					} catch (Exception ex) {
						ConsoleJS.CLIENT.error("Item " + event.item.kjs$getId() + " dynamic tooltip error", ex);
					}
				} else {
					action.apply(event.lines);
				}
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public static void onItemTooltip(ItemTooltipEvent event) {
		var stack = event.getItemStack();

		if (stack.isEmpty()) {
			return;
		}

		var mc = Minecraft.getInstance();
		var lines = event.getToolTip();
		var flags = event.getFlags();
		var sessionData = KubeSessionData.of(mc);

		var dynamicEvent = new DynamicItemTooltipsKubeEvent(stack, flags, lines, sessionData == null);

		for (var tooltip : KubeJSClient.clientItemTooltips) {
			handleItemTooltips(mc, tooltip, dynamicEvent);
		}

		if (sessionData != null) {
			for (var tooltip : sessionData.itemTooltips) {
				handleItemTooltips(mc, tooltip, dynamicEvent);
			}
		}

		var advanced = flags.isAdvanced();

		if (mc.level != null && advanced && ClientProperties.get().showComponents && dynamicEvent.alt) {
			var components = BuiltInRegistries.DATA_COMPONENT_TYPE;
			var ops = mc.level.registryAccess().createSerializationContext(NbtOps.INSTANCE);
			var errors = new ArrayList<String>(0);

			for (var entry : stack.getComponentsPatch().entrySet()) {
				var id = components.getKey(entry.getKey());

				if (id != null) {
					var line = Component.empty();
					line.append(TextIcons.icon(Component.literal("Q.")));

					if (entry.getValue().isEmpty()) {
						line.append(Component.literal("!"));
					}

					line.append(Component.literal(ID.reduce(id)).kjs$yellow());

					if (entry.getValue().isPresent()) {
						line.append(Component.literal("="));
						var errors0 = appendComponentValue(ops, line, (DataComponentType) entry.getKey(), entry.getValue().get());

						if (!errors0.isEmpty()) {
							lines.add(Component.literal(ID.reduce(id) + " errored, see log").kjs$darkRed());
							errors.add("Failed to encode value of " + id + ": " + entry.getValue().get());
							errors.addAll(errors0);
						}
					}

					lines.add(line);
				}
			}

			if (dynamicEvent.shift) {
				for (var type : stack.getPrototype()) {
					var id = components.getKey(type.type());

					if (id != null && stack.getComponentsPatch().get(type.type()) == null) {
						var line = Component.empty();
						line.append(TextIcons.icon(Component.literal("P.")));
						line.append(Component.literal(ID.reduce(id)).kjs$gray());
						line.append(Component.literal("="));
						var errors0 = appendComponentValue(ops, line, (DataComponentType) type.type(), type.value());

						if (!errors0.isEmpty()) {
							lines.add(Component.literal(ID.reduce(id) + " errored, see log").kjs$darkRed());
							errors.add("Failed to encode value of " + id + ": " + type.value());
							errors.addAll(errors0);
						}

						lines.add(line);
					}
				}
			}

			if (!errors.isEmpty() && !lastComponentError.equals(errors)) {
				lastComponentError = errors;
				errors.forEach(ConsoleJS.CLIENT::error);
			}
		} else if (advanced && ClientProperties.get().showTagNames && dynamicEvent.shift) {
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

			if (!tempTagNames.isEmpty()) {
				tempTagNames.values().stream().sorted().map(TagInstance::toText).forEach(lines::add);
			}
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

		/*
		if (PlatformWrapper.isDevelopmentEnvironment()) {
			var fb = ImageGenerator.FB_CACHE.get(128);

			if (fb != null) {
				var graphics = event.getGuiGraphics();
				graphics.pose().pushPose();
				graphics.pose().translate(event.getGuiGraphics().guiWidth() - 66F - 3F, 4F, 0F);
				graphics.fill(0, 0, 66, 66, 0xFF000000);
				graphics.fill(1, 1, 65, 65, 0xFF222222);
				RenderSystem.setShader(GameRenderer::getPositionTexShader);
				RenderSystem.setShaderTexture(0, fb.getColorTextureId());
				var m = graphics.pose().last().pose();
				var builder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
				builder.addVertex(m, 1F, 1F, 10F).setUv(0F, 1F);
				builder.addVertex(m, 1F, 65F, 10F).setUv(0F, 0F);
				builder.addVertex(m, 65F, 65F, 10F).setUv(1F, 0F);
				builder.addVertex(m, 65F, 1F, 10F).setUv(1F, 1F);
				BufferUploader.drawWithShader(builder.buildOrThrow());
				graphics.pose().popPose();
			}
		}
		*/
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
		var s = KubeJSGameClientEventHandler.setScreen(event.getScreen());

		if (s != null && event.getScreen() != s) {
			event.setNewScreen(s);
		}
	}

	@SubscribeEvent
	public static void tagsUpdated(TagsUpdatedEvent event) {
		if (event.getUpdateCause() == TagsUpdatedEvent.UpdateCause.CLIENT_PACKET_RECEIVED && Minecraft.getInstance().screen instanceof KubeJSErrorScreen screen && screen.scriptType == ScriptType.SERVER) {
			Minecraft.getInstance().kjs$runCommand("kubejs errors server");
		}
	}
}