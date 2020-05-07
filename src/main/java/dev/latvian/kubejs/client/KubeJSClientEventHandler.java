package dev.latvian.kubejs.client;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.block.BlockJS;
import dev.latvian.kubejs.core.ImageButtonKJS;
import dev.latvian.kubejs.item.BlockItemJS;
import dev.latvian.kubejs.item.ItemJS;
import dev.latvian.kubejs.player.AttachPlayerDataEvent;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.text.Text;
import dev.latvian.kubejs.util.FieldJS;
import dev.latvian.kubejs.util.Overlay;
import dev.latvian.kubejs.util.UtilsJS;
import dev.latvian.kubejs.world.AttachWorldDataEvent;
import dev.latvian.kubejs.world.ClientWorldJS;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.recipebook.IRecipeShownListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
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
import java.util.List;

/**
 * @author LatvianModder
 */
public class KubeJSClientEventHandler
{
	private static FieldJS<List<Widget>> buttons;
	private static final ResourceLocation RECIPE_BUTTON_TEXTURE = new ResourceLocation("textures/gui/recipe_button.png");

	public void init()
	{
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		MinecraftForge.EVENT_BUS.addListener(this::debugInfo);
		MinecraftForge.EVENT_BUS.addListener(this::itemTooltip);
		MinecraftForge.EVENT_BUS.addListener(this::clientTick);
		MinecraftForge.EVENT_BUS.addListener(this::loggedIn);
		MinecraftForge.EVENT_BUS.addListener(this::loggedOut);
		MinecraftForge.EVENT_BUS.addListener(this::respawn);
		MinecraftForge.EVENT_BUS.addListener(this::inGameScreenDraw);
		MinecraftForge.EVENT_BUS.addListener(this::guiScreenDraw);
		MinecraftForge.EVENT_BUS.addListener(this::guiPostInit);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::itemColors);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::blockColors);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::postAtlasStitch);
	}

	private void setup(FMLClientSetupEvent event)
	{
		for (BlockJS block : BlockJS.KUBEJS_BLOCKS.values())
		{
			switch (block.properties.renderType)
			{
				case "cutout":
					RenderTypeLookup.setRenderLayer(block, RenderType.getCutout());
					break;
				case "cutout_mipped":
					RenderTypeLookup.setRenderLayer(block, RenderType.getCutoutMipped());
					break;
				case "translucent":
					RenderTypeLookup.setRenderLayer(block, RenderType.getTranslucent());
					break;
				//default:
				//	RenderTypeLookup.setRenderLayer(block, RenderType.getSolid());
			}
		}
	}

	private void debugInfo(RenderGameOverlayEvent.Text event)
	{
		if (Minecraft.getInstance().player != null)
		{
			new DebugInfoEventJS(event).post(ScriptType.CLIENT, KubeJSEvents.CLIENT_DEBUG_INFO);
		}
	}

	private void itemTooltip(ItemTooltipEvent event)
	{
		if (ClientProperties.get().showTagNames && Minecraft.getInstance().gameSettings.advancedItemTooltips && Screen.hasShiftDown())
		{
			for (ResourceLocation tag : event.getItemStack().getItem().getTags())
			{
				event.getToolTip().add(new StringTextComponent(" #" + tag).applyTextStyle(TextFormatting.DARK_GRAY));
			}
		}

		new ClientItemTooltipEventJS(event).post(ScriptType.CLIENT, KubeJSEvents.CLIENT_ITEM_TOOLTIP);
	}

	private void clientTick(TickEvent.ClientTickEvent event)
	{
		if (Minecraft.getInstance().player != null)
		{
			new ClientTickEventJS(ClientWorldJS.instance.clientPlayerData.getPlayer()).post(KubeJSEvents.CLIENT_TICK);
		}
	}

	private void loggedIn(ClientPlayerNetworkEvent.LoggedInEvent event)
	{
		ClientWorldJS.instance = new ClientWorldJS(Minecraft.getInstance(), event.getPlayer());
		MinecraftForge.EVENT_BUS.post(new AttachWorldDataEvent(ClientWorldJS.instance));
		MinecraftForge.EVENT_BUS.post(new AttachPlayerDataEvent(ClientWorldJS.instance.clientPlayerData));
		new ClientLoggedInEventJS(ClientWorldJS.instance.clientPlayerData.getPlayer()).post(KubeJSEvents.CLIENT_LOGGED_IN);
	}

	private void loggedOut(ClientPlayerNetworkEvent.LoggedOutEvent event)
	{
		if (ClientWorldJS.instance != null)
		{
			new ClientLoggedInEventJS(ClientWorldJS.instance.clientPlayerData.getPlayer()).post(KubeJSEvents.CLIENT_LOGGED_OUT);
		}

		ClientWorldJS.instance = null;
		KubeJSClient.activeOverlays.clear();
	}

	private void respawn(ClientPlayerNetworkEvent.RespawnEvent event)
	{
		ClientWorldJS.instance = new ClientWorldJS(Minecraft.getInstance(), event.getNewPlayer());
		MinecraftForge.EVENT_BUS.post(new AttachWorldDataEvent(ClientWorldJS.instance));
		MinecraftForge.EVENT_BUS.post(new AttachPlayerDataEvent(ClientWorldJS.instance.clientPlayerData));
	}

	private int drawOverlay(Minecraft mc, int maxWidth, int x, int y, int p, Overlay o, boolean inv)
	{
		List<String> list = new ArrayList<>();
		int l = 10;

		for (Text t : o.text)
		{
			list.addAll(mc.fontRenderer.listFormattedStringToWidth(t.getFormattedString(), maxWidth));
		}

		int mw = 0;

		for (String s : list)
		{
			mw = Math.max(mw, mc.fontRenderer.getStringWidth(s));
		}

		if (mw == 0)
		{
			return 0;
		}

		int w = mw + p * 2;
		int h = list.size() * l + p * 2 - 2;
		int col = 0xFF000000 | o.color;
		int r = (col >> 16) & 0xFF;
		int g = (col >> 8) & 0xFF;
		int b = col & 0xFF;

		RenderSystem.disableTexture();
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

		//o.color.withAlpha(200).draw(spx, spy, mw + p * 2, list.size() * l + p * 2 - 2);

		if (inv)
		{
			addRectToBuffer(buffer, x, y, w, h, r, g, b, 255);
			addRectToBuffer(buffer, x, y + 1, 1, h - 2, 0, 0, 0, 80);
			addRectToBuffer(buffer, x + w - 1, y + 1, 1, h - 2, 0, 0, 0, 80);
			addRectToBuffer(buffer, x, y, w, 1, 0, 0, 0, 80);
			addRectToBuffer(buffer, x, y + h - 1, w, 1, 0, 0, 0, 80);
		}
		else
		{
			addRectToBuffer(buffer, x, y, w, h, r, g, b, 200);
			addRectToBuffer(buffer, x, y + 1, 1, h - 2, r, g, b, 255);
			addRectToBuffer(buffer, x + w - 1, y + 1, 1, h - 2, r, g, b, 255);
			addRectToBuffer(buffer, x, y, w, 1, r, g, b, 255);
			addRectToBuffer(buffer, x, y + h - 1, w, 1, r, g, b, 255);
		}

		tessellator.draw();
		RenderSystem.enableTexture();

		for (int i = 0; i < list.size(); i++)
		{
			mc.fontRenderer.drawStringWithShadow(list.get(i), x + p, y + i * l + p, 0xFFFFFFFF);
		}

		return list.size() * l + p * 2 + (p - 2);
	}

	private void addRectToBuffer(BufferBuilder buffer, int x, int y, int w, int h, int r, int g, int b, int a)
	{
		buffer.pos(x, y + h, 0D).color(r, g, b, a).endVertex();
		buffer.pos(x + w, y + h, 0D).color(r, g, b, a).endVertex();
		buffer.pos(x + w, y, 0D).color(r, g, b, a).endVertex();
		buffer.pos(x, y, 0D).color(r, g, b, a).endVertex();
	}

	private void inGameScreenDraw(RenderGameOverlayEvent.Post event)
	{
		if (KubeJSClient.activeOverlays.isEmpty() || event.getType() != RenderGameOverlayEvent.ElementType.ALL)
		{
			return;
		}

		Minecraft mc = Minecraft.getInstance();

		if (mc.gameSettings.showDebugInfo || mc.currentScreen != null)
		{
			return;
		}

		RenderSystem.pushMatrix();
		RenderSystem.translatef(0, 0, 800);
		RenderSystem.enableBlend();
		RenderSystem.disableLighting();

		int maxWidth = mc.getMainWindow().getScaledWidth() / 4;
		int p = 4;
		int spx = p;
		int spy = p;

		for (Overlay o : KubeJSClient.activeOverlays.values())
		{
			spy += drawOverlay(mc, maxWidth, spx, spy, p, o, false);
		}

		RenderSystem.popMatrix();
	}

	private void guiScreenDraw(GuiScreenEvent.DrawScreenEvent.Post event)
	{
		if (KubeJSClient.activeOverlays.isEmpty())
		{
			return;
		}

		Minecraft mc = Minecraft.getInstance();

		RenderSystem.pushMatrix();
		RenderSystem.translatef(0, 0, 800);
		RenderSystem.enableBlend();
		RenderSystem.disableLighting();

		int maxWidth = mc.getMainWindow().getScaledWidth() / 4;
		int p = 4;
		int spx = p;
		int spy = p;

		if (buttons == null)
		{
			buttons = UtilsJS.getField(Screen.class, "buttons");
		}

		while (isOver(buttons.get(event.getGui()).orElse(Collections.emptyList()), spx, spy))
		{
			spy += 16;
		}

		for (Overlay o : KubeJSClient.activeOverlays.values())
		{
			if (o.alwaysOnTop)
			{
				spy += drawOverlay(mc, maxWidth, spx, spy, p, o, true);
			}
		}

		RenderSystem.popMatrix();
	}

	private boolean isOver(List<Widget> list, int x, int y)
	{
		for (Widget w : list)
		{
			if (w.visible && x >= w.x && y >= w.y && x < w.x + w.getWidth() && y < w.y + w.getHeight())
			{
				return true;
			}
		}

		return false;
	}

	private void guiPostInit(GuiScreenEvent.InitGuiEvent.Post event)
	{
		if (ClientProperties.get().disableRecipeBook && event.getGui() instanceof IRecipeShownListener)
		{
			for (Widget widget : event.getWidgetList())
			{
				if (widget instanceof ImageButtonKJS && RECIPE_BUTTON_TEXTURE.equals(((ImageButtonKJS) widget).getButtonTextureKJS()))
				{
					event.removeWidget(widget);
					return;
				}
			}
		}
	}

	private void itemColors(ColorHandlerEvent.Item event)
	{
		for (ItemJS item : ItemJS.KUBEJS_ITEMS.values())
		{
			if (!item.properties.color.isEmpty())
			{
				event.getItemColors().register((stack, index) -> item.properties.color.get(index), item);
			}
		}

		for (BlockItemJS item : BlockItemJS.KUBEJS_BLOCK_ITEMS.values())
		{
			if (!item.properties.color.isEmpty())
			{
				event.getItemColors().register((stack, index) -> item.properties.color.get(index), item);
			}
		}
	}

	private void blockColors(ColorHandlerEvent.Block event)
	{
		for (BlockJS block : BlockJS.KUBEJS_BLOCKS.values())
		{
			if (!block.properties.color.isEmpty())
			{
				event.getBlockColors().register((state, world, pos, index) -> block.properties.color.get(index), block);
			}
		}
	}

	private void postAtlasStitch(TextureStitchEvent.Post event)
	{
		if (!ClientProperties.get().exportAtlases)
		{
			return;
		}

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, event.getMap().getGlTextureId());
		int w = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
		int h = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);

		if (w <= 0 || h <= 0)
		{
			return;
		}

		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		int[] pixels = new int[w * h];

		IntBuffer result = BufferUtils.createIntBuffer(w * h);
		GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, result);
		result.get(pixels);

		image.setRGB(0, 0, w, h, pixels, 0, w);

		Path path = FMLPaths.GAMEDIR.get().resolve("kubejs/exported/" + event.getMap().getTextureLocation().getNamespace() + "/" + event.getMap().getTextureLocation().getPath());

		if (!Files.exists(path.getParent()))
		{
			try
			{
				Files.createDirectories(path.getParent());
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
				return;
			}
		}

		if (!Files.exists(path))
		{
			try
			{
				Files.createFile(path);
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
				return;
			}
		}

		try (OutputStream stream = Files.newOutputStream(path))
		{
			ImageIO.write(image, "PNG", stream);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
}