package dev.latvian.mods.kubejs.client;

import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.hooks.client.screen.ScreenAccess;
import dev.architectury.hooks.fluid.FluidBucketHooks;
import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.bindings.event.ClientEvents;
import dev.latvian.mods.kubejs.bindings.event.ItemEvents;
import dev.latvian.mods.kubejs.client.painter.Painter;
import dev.latvian.mods.kubejs.gui.KubeJSMenus;
import dev.latvian.mods.kubejs.gui.KubeJSScreen;
import dev.latvian.mods.kubejs.item.ItemTooltipKubeEvent;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.kubejs.util.Tags;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.CustomizeGuiOverlayEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import org.jetbrains.annotations.Nullable;

import java.awt.Paint;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@EventBusSubscriber(modid = KubeJS.MOD_ID)
public class KubeJSClientEventHandler {
	private static final ResourceLocation RECIPE_BUTTON_TEXTURE = new ResourceLocation("textures/gui/recipe_button.png");
	public static Map<Item, List<ItemTooltipKubeEvent.StaticTooltipHandler>> staticItemTooltips = null;
	private final Map<ResourceLocation, TagInstance> tempTagNames = new LinkedHashMap<>();

	public void init(IEventBus bus) {
		bus.addListener(this::registerMenuScreens);

		//ClientTextureStitchEvent.POST.register(this::postAtlasStitch);
	}

	@SubscribeEvent
	private void debugInfo(CustomizeGuiOverlayEvent.DebugText event) {
		var mc = Minecraft.getInstance();
		if (mc.player != null) {
			if (ClientEvents.DEBUG_LEFT.hasListeners()) {
				ClientEvents.DEBUG_LEFT.post(new DebugInfoKubeEvent(event.getLeft()));
			}
			if (ClientEvents.DEBUG_RIGHT.hasListeners()) {
				ClientEvents.DEBUG_RIGHT.post(new DebugInfoKubeEvent(event.getRight()));
			}
		}
	}

	@SubscribeEvent
	public void onItemTooltip(ItemTooltipEvent event) {
		var stack = event.getItemStack();
		var lines = event.getToolTip();
		var tooltipContext = event.getContext();
		var flag = event.getFlags();

		if (stack.isEmpty()) {
			return;
		}

		var advanced = flag.isAdvanced();

		if (advanced && ClientProperties.get().getShowTagNames() && Screen.hasShiftDown()) {
			var addToTempTags = (Consumer<TagKey<?>>) tag -> tempTagNames.computeIfAbsent(tag.location(), TagInstance::new).registries.add(tag.registry());
			var cx = ScriptType.CLIENT.manager.get().contextFactory.enter();

			Tags.byItemStack(cx, stack).forEach(addToTempTags);

			if (stack.getItem() instanceof BlockItem item) {
				Tags.byBlock(cx, item.getBlock()).forEach(addToTempTags);
			}

			if (stack.getItem() instanceof BucketItem bucket) {
				Fluid fluid = FluidBucketHooks.getFluid(bucket);

				if (fluid != Fluids.EMPTY) {
					Tags.byFluid(cx, fluid).forEach(addToTempTags);
				}
			}

			if (stack.getItem() instanceof SpawnEggItem item) {
				Tags.byEntityType(cx, item.getType(stack)).forEach(addToTempTags);
			}

			for (var instance : tempTagNames.values()) {
				lines.add(instance.toText());
			}

			tempTagNames.clear();
		}

		if (staticItemTooltips == null) {
			staticItemTooltips = new HashMap<>();
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
	public void onClientPlayerNetwork(ClientPlayerNetworkEvent event) {
		switch (event) {
			case ClientPlayerNetworkEvent.LoggingIn loggingIn -> {
				ClientEvents.LOGGED_IN.post(ScriptType.CLIENT, new ClientKubeEvent());
			}
			case ClientPlayerNetworkEvent.LoggingOut loggingOut -> {
				ClientEvents.LOGGED_OUT.post(ScriptType.CLIENT, new ClientKubeEvent());
				Painter.INSTANCE.clear();
			}
			default -> {
				// clone does not have a handler currently
			}
		}
	}

	@SubscribeEvent
	public void onRenderGuiPost(RenderGuiEvent.Post event) {
		Painter.INSTANCE.inGameScreenDraw(event.getGuiGraphics(), event.getPartialTick());
	}

	@SubscribeEvent
	public void onRenderPost(ScreenEvent.Render.Post event) {
		Painter.INSTANCE.guiScreenDraw(event.getScreen(), event.getGuiGraphics(), event.getMouseX(), event.getMouseY(), event.getPartialTick());
	}

	@Nullable
	public static Screen setScreen(Screen screen) {
		if (screen instanceof TitleScreen && !ConsoleJS.STARTUP.errors.isEmpty() && CommonProperties.get().startupErrorGUI) {
			return new KubeJSErrorScreen(screen, ConsoleJS.STARTUP);
		}

		return screen;
	}

	@SubscribeEvent
	public void guiPostInit(ScreenEvent.Init.Post event) {
		var screen = event.getScreen();

		if (ClientProperties.get().getDisableRecipeBook() && screen instanceof RecipeUpdateListener) {
			var iterator = screen.children().iterator();
			while (iterator.hasNext()) {
				var listener = iterator.next();
				if (listener instanceof ImageButton button && button.sprites.enabled().equals(RECIPE_BUTTON_TEXTURE)) {
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

	private void registerMenuScreens(RegisterMenuScreensEvent event) {
		event.register(KubeJSMenus.MENU.get(), KubeJSScreen::new);
	}
}