package dev.latvian.mods.kubejs.client;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.event.events.client.ClientLifecycleEvent;
import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.event.events.client.ClientTooltipEvent;
import dev.architectury.hooks.client.screen.ScreenAccess;
import dev.architectury.hooks.fluid.FluidBucketHooks;
import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.bindings.event.ClientEvents;
import dev.latvian.mods.kubejs.bindings.event.ItemEvents;
import dev.latvian.mods.kubejs.client.painter.Painter;
import dev.latvian.mods.kubejs.core.ImageButtonKJS;
import dev.latvian.mods.kubejs.item.ItemTooltipEventJS;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.kubejs.util.Tags;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class KubeJSClientEventHandler {
	private static final ResourceLocation RECIPE_BUTTON_TEXTURE = new ResourceLocation("textures/gui/recipe_button.png");
	public static Map<Item, List<ItemTooltipEventJS.StaticTooltipHandler>> staticItemTooltips = null;
	private final Map<ResourceLocation, TagInstance> tempTagNames = new LinkedHashMap<>();

	public void init() {
		ClientGuiEvent.DEBUG_TEXT_LEFT.register(this::debugInfoLeft);
		ClientGuiEvent.DEBUG_TEXT_RIGHT.register(this::debugInfoRight);
		ClientTooltipEvent.ITEM.register(this::itemTooltip);
		ClientPlayerEvent.CLIENT_PLAYER_JOIN.register(this::loggedIn);
		ClientPlayerEvent.CLIENT_PLAYER_QUIT.register(this::loggedOut);
		ClientPlayerEvent.CLIENT_PLAYER_RESPAWN.register(this::respawn);
		ClientGuiEvent.RENDER_HUD.register(Painter.INSTANCE::inGameScreenDraw);
		ClientGuiEvent.RENDER_POST.register(Painter.INSTANCE::guiScreenDraw);
		ClientGuiEvent.INIT_PRE.register(this::guiPreInit);
		ClientGuiEvent.INIT_POST.register(this::guiPostInit);
		ClientLifecycleEvent.CLIENT_STARTED.register(this::clientStart);
		//ClientTextureStitchEvent.POST.register(this::postAtlasStitch);
	}

	private void debugInfoLeft(List<String> lines) {
		if (Minecraft.getInstance().player != null && ClientEvents.DEBUG_LEFT.hasListeners()) {
			ClientEvents.DEBUG_LEFT.post(ScriptType.CLIENT, new DebugInfoEventJS(lines));
		}
	}

	private void debugInfoRight(List<String> lines) {
		if (Minecraft.getInstance().player != null && ClientEvents.DEBUG_RIGHT.hasListeners()) {
			ClientEvents.DEBUG_RIGHT.post(ScriptType.CLIENT, new DebugInfoEventJS(lines));
		}
	}

	private void itemTooltip(ItemStack stack, List<Component> lines, TooltipFlag flag) {
		if (stack.isEmpty()) {
			return;
		}

		var advanced = flag.isAdvanced();

		if (advanced && ClientProperties.get().getShowTagNames() && Screen.hasShiftDown()) {
			var addToTempTags = (Consumer<TagKey<?>>) tag -> tempTagNames.computeIfAbsent(tag.location(), TagInstance::new).registries.add(tag.registry());

			Tags.byItemStack(stack).forEach(addToTempTags);

			if (stack.getItem() instanceof BlockItem item) {
				Tags.byBlock(item.getBlock()).forEach(addToTempTags);
			}

			if (stack.getItem() instanceof BucketItem bucket) {
				Fluid fluid = FluidBucketHooks.getFluid(bucket);

				if (fluid != Fluids.EMPTY) {
					Tags.byFluid(fluid).forEach(addToTempTags);
				}
			}

			if (stack.getItem() instanceof SpawnEggItem item) {
				Tags.byEntityType(item.getType(stack.getTag())).forEach(addToTempTags);
			}

			for (var instance : tempTagNames.values()) {
				lines.add(instance.toText());
			}

			tempTagNames.clear();
		}

		if (staticItemTooltips == null) {
			staticItemTooltips = new HashMap<>();
			ItemEvents.TOOLTIP.post(ScriptType.CLIENT, new ItemTooltipEventJS(staticItemTooltips));
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

	private void loggedIn(LocalPlayer player) {
		ClientEvents.LOGGED_IN.post(ScriptType.CLIENT, new ClientEventJS());
	}

	private void loggedOut(LocalPlayer player) {
		ClientEvents.LOGGED_OUT.post(ScriptType.CLIENT, new ClientEventJS());
		Painter.INSTANCE.clear();
	}

	private void respawn(LocalPlayer oldPlayer, LocalPlayer newPlayer) {
		// client respawn event
	}

	@Nullable
	public static Screen setScreen(Screen screen) {
		if (screen instanceof TitleScreen && !ConsoleJS.STARTUP.errors.isEmpty() && CommonProperties.get().startupErrorGUI) {
			return new KubeJSErrorScreen(screen, ConsoleJS.STARTUP);
		}

		return screen;
	}

	private EventResult guiPreInit(Screen screen, ScreenAccess screenAccess) {
		return EventResult.pass();
	}

	private void guiPostInit(Screen screen, ScreenAccess access) {
		if (ClientProperties.get().getDisableRecipeBook() && screen instanceof RecipeUpdateListener) {
			var iterator = screen.children().iterator();
			while (iterator.hasNext()) {
				var listener = iterator.next();
				if (listener instanceof AbstractWidget && listener instanceof ImageButtonKJS buttonKJS && RECIPE_BUTTON_TEXTURE.equals(buttonKJS.kjs$getButtonTexture())) {
					access.getRenderables().remove(listener);
					access.getNarratables().remove(listener);
					iterator.remove();
					return;
				}
			}
		}
	}

	private void clientStart(Minecraft mc) {
		ClientEvents.PARTICLE_PROVIDER_REGISTRY.post(new ParticleProviderRegistryEventJS());
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
}