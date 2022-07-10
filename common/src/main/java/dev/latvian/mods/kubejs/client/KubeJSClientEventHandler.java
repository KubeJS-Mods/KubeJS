package dev.latvian.mods.kubejs.client;

import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.event.events.client.ClientLifecycleEvent;
import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.event.events.client.ClientTextureStitchEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.event.events.client.ClientTooltipEvent;
import dev.architectury.hooks.client.screen.ScreenAccess;
import dev.architectury.hooks.fluid.FluidBucketHooks;
import dev.latvian.mods.kubejs.KubeJSEvents;
import dev.latvian.mods.kubejs.KubeJSPaths;
import dev.latvian.mods.kubejs.RegistryObjectBuilderTypes;
import dev.latvian.mods.kubejs.client.painter.Painter;
import dev.latvian.mods.kubejs.core.ImageButtonKJS;
import dev.latvian.mods.kubejs.item.ItemModelPropertiesEventJS;
import dev.latvian.mods.kubejs.item.ItemTooltipEventJS;
import dev.latvian.mods.kubejs.level.ClientLevelJS;
import dev.latvian.mods.kubejs.script.AttachDataEvent;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.util.Tags;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.texture.TextureAtlas;
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
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
public class KubeJSClientEventHandler {
	private static final ResourceLocation RECIPE_BUTTON_TEXTURE = new ResourceLocation("textures/gui/recipe_button.png");
	public static Map<Item, List<ItemTooltipEventJS.StaticTooltipHandler>> staticItemTooltips = null;
	private final Map<ResourceLocation, TagInstance> tempTagNames = new LinkedHashMap<>();

	public void init() {
		ClientLifecycleEvent.CLIENT_SETUP.register(this::clientSetup);
		ClientGuiEvent.DEBUG_TEXT_LEFT.register(this::debugInfoLeft);
		ClientGuiEvent.DEBUG_TEXT_RIGHT.register(this::debugInfoRight);
		ClientTooltipEvent.ITEM.register(this::itemTooltip);
		ClientTickEvent.CLIENT_POST.register(this::clientTick);
		ClientPlayerEvent.CLIENT_PLAYER_JOIN.register(this::loggedIn);
		ClientPlayerEvent.CLIENT_PLAYER_QUIT.register(this::loggedOut);
		ClientPlayerEvent.CLIENT_PLAYER_RESPAWN.register(this::respawn);
		ClientGuiEvent.RENDER_HUD.register(Painter.INSTANCE::inGameScreenDraw);
		ClientGuiEvent.RENDER_POST.register(Painter.INSTANCE::guiScreenDraw);
		ClientGuiEvent.INIT_POST.register(this::guiPostInit);
		ClientTextureStitchEvent.POST.register(this::postAtlasStitch);
	}

	private void clientSetup(Minecraft minecraft) {
		for (var builder : RegistryObjectBuilderTypes.ALL_BUILDERS) {
			builder.clientRegistry(() -> minecraft);
		}
		new ItemModelPropertiesEventJS().post(KubeJSEvents.ITEM_MODEL_PROPERTIES);
	}

	private void debugInfoLeft(List<String> lines) {
		if (Minecraft.getInstance().player != null) {
			DebugInfoEventJS.LEFT_EVENT.post(new DebugInfoEventJS(lines));
		}
	}

	private void debugInfoRight(List<String> lines) {
		if (Minecraft.getInstance().player != null) {
			DebugInfoEventJS.RIGHT_EVENT.post(new DebugInfoEventJS(lines));
		}
	}

	private void itemTooltip(ItemStack stack, List<Component> lines, TooltipFlag flag) {
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
			new ItemTooltipEventJS(staticItemTooltips).post(ScriptType.CLIENT, KubeJSEvents.ITEM_TOOLTIP);
		}

		for (var handler : staticItemTooltips.getOrDefault(Items.AIR, Collections.emptyList())) {
			handler.tooltip(stack, advanced, lines);
		}

		for (var handler : staticItemTooltips.getOrDefault(stack.getItem(), Collections.emptyList())) {
			handler.tooltip(stack, advanced, lines);
		}
	}

	private void clientTick(Minecraft minecraft) {
		if (Minecraft.getInstance().player != null && ClientLevelJS.getInstance() != null) {
			ClientEventJS.TICK_EVENT.post(new ClientEventJS());
		}
	}

	private void loggedIn(LocalPlayer player) {
		ClientLevelJS.setInstance(new ClientLevelJS(Minecraft.getInstance(), player));
		AttachDataEvent.forLevel(ClientLevelJS.getInstance()).invoke();
		AttachDataEvent.forPlayer(ClientLevelJS.getInstance().clientPlayerData).invoke();
		ClientEventJS.LOGGED_IN_EVENT.post(new ClientEventJS());
	}

	private void loggedOut(LocalPlayer player) {
		if (ClientLevelJS.getInstance() != null) {
			ClientEventJS.LOGGED_OUT_EVENT.post(new ClientEventJS());
		}

		ClientLevelJS.setInstance(null);
		Painter.INSTANCE.clear();
	}

	private void respawn(LocalPlayer oldPlayer, LocalPlayer newPlayer) {
		ClientLevelJS.setInstance(new ClientLevelJS(Minecraft.getInstance(), newPlayer));
		AttachDataEvent.forLevel(ClientLevelJS.getInstance()).invoke();
		AttachDataEvent.forPlayer(ClientLevelJS.getInstance().clientPlayerData).invoke();
	}

	private void guiPostInit(Screen screen, ScreenAccess access) {
		if (ClientProperties.get().getDisableRecipeBook() && screen instanceof RecipeUpdateListener) {
			var iterator = screen.children().iterator();
			while (iterator.hasNext()) {
				var listener = iterator.next();
				if (listener instanceof AbstractWidget && listener instanceof ImageButtonKJS buttonKJS && RECIPE_BUTTON_TEXTURE.equals(buttonKJS.getButtonTextureKJS())) {
					access.getRenderables().remove(listener);
					access.getNarratables().remove(listener);
					iterator.remove();
					return;
				}
			}
		}
	}

	private void postAtlasStitch(TextureAtlas atlas) {
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

		var path = KubeJSPaths.EXPORTED.resolve(atlas.location().getNamespace() + "/" + atlas.location().getPath());

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
	}
}