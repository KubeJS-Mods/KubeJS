package dev.latvian.kubejs.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.KubeJSObjects;
import dev.latvian.kubejs.KubeJSPaths;
import dev.latvian.kubejs.block.BlockBuilder;
import dev.latvian.kubejs.core.ImageButtonKJS;
import dev.latvian.kubejs.fluid.FluidBuilder;
import dev.latvian.kubejs.item.ItemBuilder;
import dev.latvian.kubejs.item.ItemTooltipEventJS;
import dev.latvian.kubejs.item.OldItemTooltipEventJS;
import dev.latvian.kubejs.player.AttachPlayerDataEvent;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.text.Text;
import dev.latvian.kubejs.util.Overlay;
import dev.latvian.kubejs.util.Tags;
import dev.latvian.kubejs.world.AttachWorldDataEvent;
import dev.latvian.kubejs.world.ClientWorldJS;
import me.shedaniel.architectury.event.events.GuiEvent;
import me.shedaniel.architectury.event.events.TextureStitchEvent;
import me.shedaniel.architectury.event.events.TooltipEvent;
import me.shedaniel.architectury.event.events.client.ClientPlayerEvent;
import me.shedaniel.architectury.event.events.client.ClientTickEvent;
import me.shedaniel.architectury.hooks.ScreenHooks;
import me.shedaniel.architectury.registry.ColorHandlers;
import me.shedaniel.architectury.registry.RenderTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author LatvianModder
 */
public class KubeJSClientEventHandler {
	private static final ResourceLocation RECIPE_BUTTON_TEXTURE = new ResourceLocation("textures/gui/recipe_button.png");
	public static Map<Item, List<ItemTooltipEventJS.StaticTooltipHandler>> staticItemTooltips = null;

	public void init() {
		setup();
		GuiEvent.DEBUG_TEXT_LEFT.register(this::debugInfoLeft);
		GuiEvent.DEBUG_TEXT_RIGHT.register(this::debugInfoRight);
		TooltipEvent.ITEM.register(this::itemTooltip);
		ClientTickEvent.CLIENT_POST.register(this::clientTick);
		ClientPlayerEvent.CLIENT_PLAYER_JOIN.register(this::loggedIn);
		ClientPlayerEvent.CLIENT_PLAYER_QUIT.register(this::loggedOut);
		ClientPlayerEvent.CLIENT_PLAYER_RESPAWN.register(this::respawn);
		GuiEvent.RENDER_HUD.register(this::inGameScreenDraw);
		GuiEvent.RENDER_POST.register(this::guiScreenDraw);
		GuiEvent.INIT_POST.register(this::guiPostInit);
		blockColors();
		itemColors();
		TextureStitchEvent.POST.register(this::postAtlasStitch);
	}

	private void setup() {
		for (BlockBuilder builder : KubeJSObjects.BLOCKS.values()) {
			switch (builder.renderType) {
				case "cutout":
					RenderTypes.register(RenderType.cutout(), builder.block);
					break;
				case "cutout_mipped":
					RenderTypes.register(RenderType.cutoutMipped(), builder.block);
					break;
				case "translucent":
					RenderTypes.register(RenderType.translucent(), builder.block);
					break;
				//default:
				//	RenderTypeLookup.setRenderLayer(block, RenderType.getSolid());
			}
		}
	}

	private void debugInfoLeft(List<String> lines) {
		if (Minecraft.getInstance().player != null) {
			new DebugInfoEventJS(lines).post(ScriptType.CLIENT, KubeJSEvents.CLIENT_DEBUG_INFO_LEFT);
		}
	}

	private void debugInfoRight(List<String> lines) {
		if (Minecraft.getInstance().player != null) {
			new DebugInfoEventJS(lines).post(ScriptType.CLIENT, KubeJSEvents.CLIENT_DEBUG_INFO_RIGHT);
		}
	}

	private void itemTooltip(ItemStack stack, List<Component> lines, TooltipFlag flag) {
		boolean advanced = flag.isAdvanced();

		if (advanced && ClientProperties.get().showTagNames && Screen.hasShiftDown()) {
			for (ResourceLocation tag : Tags.byItemStack(stack)) {
				lines.add(new TextComponent(" #" + tag).withStyle(ChatFormatting.DARK_GRAY));
			}
		}

		if (staticItemTooltips == null) {
			staticItemTooltips = new HashMap<>();
			new ItemTooltipEventJS(staticItemTooltips).post(ScriptType.CLIENT, KubeJSEvents.ITEM_TOOLTIP);
		}

		for (ItemTooltipEventJS.StaticTooltipHandler h : staticItemTooltips.getOrDefault(Items.AIR, Collections.emptyList())) {
			h.tooltip(stack, advanced, lines);
		}

		for (ItemTooltipEventJS.StaticTooltipHandler h : staticItemTooltips.getOrDefault(stack.getItem(), Collections.emptyList())) {
			h.tooltip(stack, advanced, lines);
		}

		// TODO: Remove me
		new OldItemTooltipEventJS(stack, lines, advanced).post(ScriptType.CLIENT, "client.item_tooltip");
	}

	private void clientTick(Minecraft minecraft) {
		if (Minecraft.getInstance().player != null && ClientWorldJS.getInstance() != null) {
			new ClientTickEventJS(ClientWorldJS.getInstance().clientPlayerData.getPlayer()).post(KubeJSEvents.CLIENT_TICK);
		}
	}

	private void loggedIn(LocalPlayer player) {
		ClientWorldJS.setInstance(new ClientWorldJS(Minecraft.getInstance(), player));
		AttachWorldDataEvent.EVENT.invoker().accept(new AttachWorldDataEvent(ClientWorldJS.getInstance()));
		AttachPlayerDataEvent.EVENT.invoker().accept(new AttachPlayerDataEvent(ClientWorldJS.getInstance().clientPlayerData));
		new ClientLoggedInEventJS(ClientWorldJS.getInstance().clientPlayerData.getPlayer()).post(KubeJSEvents.CLIENT_LOGGED_IN);
	}

	private void loggedOut(LocalPlayer player) {
		if (ClientWorldJS.getInstance() != null) {
			new ClientLoggedInEventJS(ClientWorldJS.getInstance().clientPlayerData.getPlayer()).post(KubeJSEvents.CLIENT_LOGGED_OUT);
		}

		ClientWorldJS.setInstance(null);
		KubeJSClient.activeOverlays.clear();
	}

	private void respawn(LocalPlayer oldPlayer, LocalPlayer newPlayer) {
		ClientWorldJS.setInstance(new ClientWorldJS(Minecraft.getInstance(), newPlayer));
		AttachWorldDataEvent.EVENT.invoker().accept(new AttachWorldDataEvent(ClientWorldJS.getInstance()));
		AttachPlayerDataEvent.EVENT.invoker().accept(new AttachPlayerDataEvent(ClientWorldJS.getInstance().clientPlayerData));
	}

	private int drawOverlay(Minecraft mc, PoseStack matrixStack, int maxWidth, int x, int y, int p, Overlay o, boolean inv) {
		List<FormattedCharSequence> list = new ArrayList<>();
		int l = 10;

		for (Text t : o.text) {
			list.addAll(mc.font.split(t.component(), maxWidth));
		}

		int mw = 0;

		for (FormattedCharSequence s : list) {
			mw = Math.max(mw, mc.font.width(s));
		}

		if (mw == 0) {
			return 0;
		}

		int w = mw + p * 2;
		int h = list.size() * l + p * 2 - 2;
		int col = 0xFF000000 | o.color;
		int r = (col >> 16) & 0xFF;
		int g = (col >> 8) & 0xFF;
		int b = col & 0xFF;

		RenderSystem.disableTexture();
		Tesselator tessellator = Tesselator.getInstance();
		BufferBuilder buffer = tessellator.getBuilder();
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormat.POSITION_COLOR);

		//o.color.withAlpha(200).draw(spx, spy, mw + p * 2, list.size() * l + p * 2 - 2);

		if (inv) {
			addRectToBuffer(buffer, x, y, w, h, r, g, b, 255);
			addRectToBuffer(buffer, x, y + 1, 1, h - 2, 0, 0, 0, 80);
			addRectToBuffer(buffer, x + w - 1, y + 1, 1, h - 2, 0, 0, 0, 80);
			addRectToBuffer(buffer, x, y, w, 1, 0, 0, 0, 80);
			addRectToBuffer(buffer, x, y + h - 1, w, 1, 0, 0, 0, 80);
		} else {
			addRectToBuffer(buffer, x, y, w, h, r, g, b, 200);
			addRectToBuffer(buffer, x, y + 1, 1, h - 2, r, g, b, 255);
			addRectToBuffer(buffer, x + w - 1, y + 1, 1, h - 2, r, g, b, 255);
			addRectToBuffer(buffer, x, y, w, 1, r, g, b, 255);
			addRectToBuffer(buffer, x, y + h - 1, w, 1, r, g, b, 255);
		}

		tessellator.end();
		RenderSystem.enableTexture();

		for (int i = 0; i < list.size(); i++) {
			mc.font.drawShadow(matrixStack, list.get(i), x + p, y + i * l + p, 0xFFFFFFFF);
		}

		return list.size() * l + p * 2 + (p - 2);
	}

	private void addRectToBuffer(BufferBuilder buffer, int x, int y, int w, int h, int r, int g, int b, int a) {
		buffer.vertex(x, y + h, 0D).color(r, g, b, a).endVertex();
		buffer.vertex(x + w, y + h, 0D).color(r, g, b, a).endVertex();
		buffer.vertex(x + w, y, 0D).color(r, g, b, a).endVertex();
		buffer.vertex(x, y, 0D).color(r, g, b, a).endVertex();
	}

	private void inGameScreenDraw(PoseStack matrices, float delta) {
		if (KubeJSClient.activeOverlays.isEmpty()) {
			return;
		}

		Minecraft mc = Minecraft.getInstance();

		if (mc.options.renderDebug || mc.screen != null) {
			return;
		}

		PoseStack matrixStack = new PoseStack();
		matrixStack.translate(0, 0, 800);

		RenderSystem.enableBlend();
		//RenderSystem.disableLighting();

		int maxWidth = mc.getWindow().getGuiScaledWidth() / 4;
		int p = 4;
		int spx = p;
		int spy = p;

		for (Overlay o : KubeJSClient.activeOverlays.values()) {
			spy += drawOverlay(mc, matrixStack, maxWidth, spx, spy, p, o, false);
		}
	}

	private void guiScreenDraw(Screen screen, PoseStack matrices, int mouseX, int mouseY, float delta) {
		if (KubeJSClient.activeOverlays.isEmpty()) {
			return;
		}

		Minecraft mc = Minecraft.getInstance();
		PoseStack matrixStack = new PoseStack();
		matrixStack.translate(0, 0, 800);

		RenderSystem.enableBlend();
		//RenderSystem.disableLighting();

		int maxWidth = mc.getWindow().getGuiScaledWidth() / 4;
		int p = 4;
		int spx = p;
		int spy = p;

		List<AbstractWidget> buttons = ScreenHooks.getButtons(screen);

		while (isOver(buttons, spx, spy)) {
			spy += 16;
		}

		for (Overlay o : KubeJSClient.activeOverlays.values()) {
			if (o.alwaysOnTop) {
				spy += drawOverlay(mc, matrixStack, maxWidth, spx, spy, p, o, true);
			}
		}
	}

	private boolean isOver(List<AbstractWidget> list, int x, int y) {
		for (AbstractWidget w : list) {
			if (w.visible && x >= w.x && y >= w.y && x < w.x + w.getWidth() && y < w.y + w.getHeight()) //getWidth_CLASH = getHeight
			{
				return true;
			}
		}

		return false;
	}

	private void guiPostInit(Screen screen, List<AbstractWidget> widgets, List<GuiEventListener> children) {
		if (ClientProperties.get().disableRecipeBook && screen instanceof RecipeUpdateListener) {
			Iterator<GuiEventListener> iterator = children.iterator();
			while (iterator.hasNext()) {
				GuiEventListener listener = iterator.next();
				if (listener instanceof AbstractWidget && listener instanceof ImageButtonKJS && RECIPE_BUTTON_TEXTURE.equals(((ImageButtonKJS) listener).getButtonTextureKJS())) {
					ScreenHooks.getButtons(screen).remove(listener);
					iterator.remove();
					return;
				}
			}
		}
	}

	private void itemColors() {
		for (ItemBuilder builder : KubeJSObjects.ITEMS.values()) {
			if (!builder.color.isEmpty()) {
				ColorHandlers.registerItemColors((stack, index) -> builder.color.get(index), Objects.requireNonNull(builder.item, "Item " + builder.id + " is null!"));
			}
		}

		for (BlockBuilder builder : KubeJSObjects.BLOCKS.values()) {
			if (builder.itemBuilder != null && !builder.color.isEmpty()) {
				ColorHandlers.registerItemColors((stack, index) -> builder.color.get(index), Objects.requireNonNull(builder.itemBuilder.blockItem, "Block Item " + builder.id + " is null!"));
			}
		}

		for (FluidBuilder builder : KubeJSObjects.FLUIDS.values()) {
			if (builder.bucketColor != 0xFFFFFFFF) {
				ColorHandlers.registerItemColors((stack, index) -> index == 1 ? builder.bucketColor : 0xFFFFFFFF, Objects.requireNonNull(builder.bucketItem, "Bucket Item " + builder.id + " is null!"));
			}
		}
	}

	private void blockColors() {
		for (BlockBuilder builder : KubeJSObjects.BLOCKS.values()) {
			if (!builder.color.isEmpty()) {
				ColorHandlers.registerBlockColors((state, world, pos, index) -> builder.color.get(index), builder.block);
			}
		}
	}

	private void postAtlasStitch(TextureAtlas atlas) {
		if (!ClientProperties.get().exportAtlases) {
			return;
		}

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, atlas.getId());
		int w = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
		int h = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);

		if (w <= 0 || h <= 0) {
			return;
		}

		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		int[] pixels = new int[w * h];

		IntBuffer result = BufferUtils.createIntBuffer(w * h);
		GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, result);
		result.get(pixels);

		image.setRGB(0, 0, w, h, pixels, 0, w);

		Path path = KubeJSPaths.EXPORTED.resolve(atlas.location().getNamespace() + "/" + atlas.location().getPath());

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

		try (OutputStream stream = Files.newOutputStream(path)) {
			ImageIO.write(image, "PNG", stream);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}